# YoriCore
YoriCore 是一个基于 Chisel 的硬件设计项目，使用 Verilator 进行仿真验证。本项目提供了一套完整的构建和仿真流程，方便硬件设计和验证。

## 项目结构
```text
YoriCore/
├── mkscript             # 构建脚本目录
│   ├── Chisel.mk        # Chisel 构建脚本
│   ├── Clean.mk         # 清理脚本
│   ├── Mill.mk          # Mill 构建脚本
│   └── Verilator.mk     # Verilator 构建脚本
├── resources            # 资源目录
│   └── xxx.sv           # 仿真用的 Verilog 文件
├── sim                  # 仿真目录
│   ├── inc              # 仿真头文件目录
│   ├── src              # 仿真源文件目录
│   └── verilator.vlt    # Verilator 配置文件
├── src                  # 源代码目录
│   └── main             # 主程序目录
├── Kconfig              # 仿真配置文件
├── Makefile             # 构建文件
├── README.md            # 项目说明文件
└── build.mill           # Mill 构建配置
```

## 依赖项
本项目依赖于以下工具和库，均使用当下最新的版本，低版本软件未经测试：
- Mill 1.1.5
- Verilator 5.046
- Python 3.12.3
- python3-kconfiglib 14.1.0
- gcc build tools 13.3.0

## 快速开始
1. 安装依赖项
Ubuntu 软件包
```bash
sudo apt update
sudo apt install -y verilator python3 python3-kconfiglib build-essential libreadline-dev
```
mill 工具
```bash
curl -L https://repo1.maven.org/maven2/com/lihaoyi/mill-dist/1.1.5/mill-dist-1.1.5-mill.sh -o mill
chmod +x mill
# 可自行选择安装目录，下面仅做示例
sudo ln -sf $(pwd)/mill /usr/bin/mill
```

2. 构建项目
生成verilog代码
```bash
make chisel
```
构建仿真程序
```bash
make verilator
```
运行仿真
```bash
make run
```
