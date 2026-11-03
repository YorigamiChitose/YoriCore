#include "debug.h"
#include "isa/isa.h"
#include "macro.h"
#include "memory/memory.h"
#include <cstdlib>
#include <regex.h>

static bool eval_ok;

enum {
  TK_NOTYPE = 256,
  TK_EQ,
  TK_MINUS,
  TK_PLUS,
  TK_TIMES,
  TK_DIVIDE,
  TK_REMAINDER,
  TK_NUM,
  TK_HEXNUM,
  TK_PAR_S,
  TK_PAR_E,
  TK_REGISTER,
  TK_MINUS_ONLY,
  TK_TIMES_ONLY,
  TK_NOTEQ,
  TK_LESS_EQ,
  TK_BIGGER_EQ,
  TK_LEFTMOVE,
  TK_RIGHTMOVE,
  TK_LESS,
  TK_BIGGER,
  TK_AND,
  TK_OR,
  TK_NOT,
  TK_CSR,
  TK_PC,
};

static struct rule {
  const char *regex;
  int token_type;
} rules[] = {
    {"0[xX][0-9a-fA-F]+", TK_HEXNUM},             // hex number
    {"\\$[a-zA-Z0-9]+", TK_REGISTER},             // register
    {"[0-9]+", TK_NUM},                           // number
    {" +", TK_NOTYPE},                            // spaces
    {"\\+", TK_PLUS},                             // '+'
    {"\\-", TK_MINUS},                            // '-'
    {"\\*", TK_TIMES},                            // '*'
    {"\\/", TK_DIVIDE},                           // '/'
    {"\\%", TK_REMAINDER},                        // '%'
    {"\\(", TK_PAR_S},                            // '('
    {"\\)", TK_PAR_E},                            // ')'
    {"==", TK_EQ},                                // '=='
    {"!=", TK_NOTEQ},                             // '!='
    {"<=", TK_LESS_EQ},                           // '<='
    {">=", TK_BIGGER_EQ},                         // '>='
    {"<<", TK_LEFTMOVE},                          // '<<'
    {">>", TK_RIGHTMOVE},                         // '>>'
    {"<", TK_LESS},                               // '<'
    {">", TK_BIGGER},                             // '>'
    {"&&", TK_AND},                               // '&&'
    {"\\|\\|", TK_OR},                            // '||'
    {"!", TK_NOT},                                // '!'
    {"m[a-z]+", TK_CSR},                          // csr
    {"pc", TK_PC},                                // pc
};

#define NR_REGEX ARRLEN(rules)

static regex_t re[NR_REGEX] = {};

void init_regex(void) {
  int i;
  char error_msg[128];
  int ret;

  for (i = 0; i < NR_REGEX; i++) {
    ret = regcomp(&re[i], rules[i].regex, REG_EXTENDED);
    if (ret != 0) {
      regerror(ret, &re[i], error_msg, 128);
      panic("regex compilation failed: %s\n%s", error_msg, rules[i].regex);
    }
  }
}

#define TOKENS_SIZE 64
#define TOKENSTR_SIZE 64

typedef struct token {
  int type;
  char str[TOKENSTR_SIZE];
} Token;

static Token tokens[TOKENS_SIZE] __attribute__((used)) = {};
static int nr_token __attribute__((used)) = 0;

bool is_only(int addr);
int get_operator_priority(int operator_type);
int cut_expression(int start, int end);
bool pair_first_bracket(int start, int end);

static bool make_token(char *e) {
  int position = 0;
  int i;
  regmatch_t pmatch;

  memset(tokens, 0, sizeof(tokens));
  nr_token = 0;

  while (e[position] != '\0') {
    for (i = 0; i < NR_REGEX; i++) {
      if (regexec(&re[i], e + position, 1, &pmatch, 0) == 0 &&
          pmatch.rm_so == 0) {
        char *substr_start = e + position;
        int substr_len = pmatch.rm_eo;

        position += substr_len;

        if (nr_token >= TOKENS_SIZE) {
          Log("Error! Too many tokens.");
          return false;
        }
        if (substr_len >= TOKENSTR_SIZE) {
          Log("Error! Token too long.");
          return false;
        }

        switch (rules[i].token_type) {
        case TK_DIVIDE:
        case TK_PLUS:
        case TK_NUM:
        case TK_HEXNUM:
        case TK_REGISTER:
        case TK_PAR_S:
        case TK_PAR_E:
        case TK_EQ:
        case TK_NOTEQ:
        case TK_LESS_EQ:
        case TK_BIGGER_EQ:
        case TK_LEFTMOVE:
        case TK_RIGHTMOVE:
        case TK_LESS:
        case TK_BIGGER:
        case TK_AND:
        case TK_OR:
        case TK_NOT:
        case TK_REMAINDER:
        case TK_CSR:
        case TK_PC:
          tokens[nr_token].type = rules[i].token_type;
          strncpy(tokens[nr_token++].str, substr_start, substr_len);
          break;
        case TK_TIMES:
          if (is_only(nr_token)) {
            tokens[nr_token].type = TK_TIMES_ONLY;
            strncpy(tokens[nr_token++].str, substr_start, substr_len);
          } else {
            tokens[nr_token].type = rules[i].token_type;
            strncpy(tokens[nr_token++].str, substr_start, substr_len);
          }
          break;
        case TK_MINUS:
          if (is_only(nr_token)) {
            tokens[nr_token].type = TK_MINUS_ONLY;
            strncpy(tokens[nr_token++].str, substr_start, substr_len);
          } else {
            tokens[nr_token].type = rules[i].token_type;
            strncpy(tokens[nr_token++].str, substr_start, substr_len);
          }
          break;
        default:
          break;
        }

        break;
      }
    }

    if (i == NR_REGEX) {
      printf("no match at position %d\n%s\n%*.s^\n", position, e, position, "");
      return false;
    }
  }

  return true;
}

word_t expression_value(int start, int end) {
  if (start > end) {
    Log("Error! Expression position error.");
    eval_ok = false;
    return 0;
  }
  if (pair_first_bracket(start, end)) {
    return expression_value(start + 1, end - 1);
  }
  if (start == end) {
    word_t answer = 0;
    bool flag = true;
    switch (tokens[start].type) {
    case TK_NUM:
      answer = atoi(tokens[start].str);
      return answer;
    case TK_HEXNUM:
      sscanf(tokens[start].str, FMT_WORD, &answer);
      return answer;
    case TK_REGISTER:
      answer = isa_reg_str2val(tokens[start].str + 1, &flag);
      if (flag) {
        return answer;
      }
      eval_ok = false;
      return 0;
    case TK_CSR:
      if (!strcmp(tokens[start].str, "mepc")) {
        return cpu.csr.mepc;
      }
      if (!strcmp(tokens[start].str, "mcause")) {
        return cpu.csr.mcause;
      }
      if (!strcmp(tokens[start].str, "mstatus")) {
        return cpu.csr.mstatus;
      }
      if (!strcmp(tokens[start].str, "mtvec")) {
        return cpu.csr.mtvec;
      }
      break;
    case TK_PC:
      return cpu.pc;
    default:
      break;
    }
    Log("Error! Invalid expression.");
    eval_ok = false;
    return 0;
  }

  int cut_add = cut_expression(start, end);
  if (cut_add == -1) {
    Log("Error! Invalid expression.");
    eval_ok = false;
    return 0;
  }

  switch (tokens[cut_add].type) {
  case TK_MINUS_ONLY:
    return (word_t)(-1) * expression_value(cut_add + 1, end);
  case TK_TIMES_ONLY:
    return pmem_read(expression_value(cut_add + 1, end), 4);
  case TK_NOT:
    return !expression_value(cut_add + 1, end);

  case TK_PLUS:
    return expression_value(start, cut_add - 1) +
           expression_value(cut_add + 1, end);
  case TK_MINUS:
    return expression_value(start, cut_add - 1) -
           expression_value(cut_add + 1, end);
  case TK_TIMES:
    return expression_value(start, cut_add - 1) *
           expression_value(cut_add + 1, end);
  case TK_REMAINDER: {
    word_t rhs = expression_value(cut_add + 1, end);
    if (!eval_ok) {
      return 0;
    }
    if (rhs == 0) {
      Log("Error! Number division by zero.");
      eval_ok = false;
      return 0;
    }
    return expression_value(start, cut_add - 1) % rhs;
  }
  case TK_DIVIDE: {
    word_t rhs = expression_value(cut_add + 1, end);
    if (!eval_ok) {
      return 0;
    }
    if (rhs == 0) {
      Log("Error! Number division by zero.");
      eval_ok = false;
      return 0;
    }
    return expression_value(start, cut_add - 1) / rhs;
  }
  case TK_BIGGER:
    return (expression_value(start, cut_add - 1) >
            expression_value(cut_add + 1, end));
  case TK_LESS:
    return (expression_value(start, cut_add - 1) <
            expression_value(cut_add + 1, end));
  case TK_BIGGER_EQ:
    return (expression_value(start, cut_add - 1) >=
            expression_value(cut_add + 1, end));
  case TK_LESS_EQ:
    return (expression_value(start, cut_add - 1) <=
            expression_value(cut_add + 1, end));
  case TK_RIGHTMOVE:
    return (expression_value(start, cut_add - 1) >>
            expression_value(cut_add + 1, end));
  case TK_LEFTMOVE:
    return (expression_value(start, cut_add - 1)
            << expression_value(cut_add + 1, end));
  case TK_NOTEQ:
    return (expression_value(start, cut_add - 1) !=
            expression_value(cut_add + 1, end));
  case TK_EQ:
    return (expression_value(start, cut_add - 1) ==
            expression_value(cut_add + 1, end));
  case TK_AND:
    return (expression_value(start, cut_add - 1) &&
            expression_value(cut_add + 1, end));
  case TK_OR:
    return (expression_value(start, cut_add - 1) ||
            expression_value(cut_add + 1, end));
  default:
    break;
  }

  Log("Error! Invalid expression.");
  eval_ok = false;
  return 0;
}

bool is_only(int addr) {
  if (addr == 0) {
    return true;
  }
  switch (tokens[addr - 1].type) {
  case TK_PLUS:
  case TK_MINUS:
  case TK_TIMES:
  case TK_DIVIDE:
  case TK_REMAINDER:
  case TK_MINUS_ONLY:
  case TK_TIMES_ONLY:
  case TK_NOT:
  case TK_PAR_S:
  case TK_EQ:
  case TK_NOTEQ:
  case TK_LESS:
  case TK_BIGGER:
  case TK_LESS_EQ:
  case TK_BIGGER_EQ:
  case TK_LEFTMOVE:
  case TK_RIGHTMOVE:
  case TK_AND:
  case TK_OR:
    return true;
  default:
    return false;
  }
}

int get_operator_priority(int operator_type) {
  switch (operator_type) {
  case TK_NOT:
  case TK_MINUS_ONLY:
  case TK_TIMES_ONLY:
    return 8;
  case TK_TIMES:
  case TK_DIVIDE:
  case TK_REMAINDER:
    return 7;
  case TK_PLUS:
  case TK_MINUS:
    return 6;
  case TK_LEFTMOVE:
  case TK_RIGHTMOVE:
    return 5;
  case TK_LESS_EQ:
  case TK_BIGGER_EQ:
  case TK_LESS:
  case TK_BIGGER:
    return 4;
  case TK_NOTEQ:
  case TK_EQ:
    return 3;
  case TK_AND:
    return 2;
  case TK_OR:
    return 1;
  }
  return 0xff;
}

int cut_expression(int start, int end) {
  int cnt = 0;
  int add = -1;
  for (int i = start; i <= end; i++) {
    switch (tokens[i].type) {
    case TK_NUM:
    case TK_HEXNUM:
    case TK_REGISTER:
    case TK_CSR:
    case TK_PC:
      break;
    case TK_PAR_S:
      ++cnt;
      break;
    case TK_PAR_E:
      --cnt;
      break;
    default:
      if (cnt != 0) {
        break;
      }
      if (add == -1 ||
          (get_operator_priority(tokens[i].type) <=
               get_operator_priority(tokens[add].type) &&
           !(get_operator_priority(tokens[i].type) == 8 &&
             get_operator_priority(tokens[add].type) == 8))) {
        add = i;
      }
      break;
    }
  }
  return add;
}

bool pair_first_bracket(int start, int end) {
  if (tokens[start].type != TK_PAR_S || tokens[end].type != TK_PAR_E) {
    return false;
  }
  int cnt = 0;
  for (int i = start; i <= end; i++) {
    switch (tokens[i].type) {
    case TK_PAR_S:
      ++cnt;
      break;
    case TK_PAR_E:
      --cnt;
      if (cnt == 0 && i != end) {
        return false;
      }
      break;
    default:
      break;
    }
  }
  return true;
}

word_t expr(char *e, bool *success) {
  if (!make_token(e) || nr_token == 0) {
    *success = false;
    return 0;
  }
  eval_ok = true;
  word_t ret = expression_value(0, nr_token - 1);
  *success = eval_ok;
  return ret;
}
