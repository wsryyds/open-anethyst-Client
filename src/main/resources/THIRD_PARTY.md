# THIRD_PARTY.md — 第三方内容清单

本项目整体许可为 **All Rights Reserved，并授予有限的终端用户运行许可**（见 `LICENSE`）：允许免费下载、安装、个人非商业使用，禁止再分发、修改与反向工程。本项目不开源，不提供源代码。下表中的组件不受此声明覆盖，各自仍按上游许可分发。许可全文见 `NOTICE.txt` 与 `licenses/`。

## 一、随 jar 分发的第三方组件

| 组件 | 位置 | 许可 | 版权 |
|---|---|---|---|
| JNA 5.17.0 | `META-INF/jars/jna-win-5.17.0.jar`（嵌套 jar，Windows 输入法修复用；**已裁剪**：只保留 `win32-x86` / `win32-x86-64` / `win32-aarch64` 的 `jnidispatch` 原生库，其余平台原生库与签名文件已删除，`.class` 未做任何改动） | Apache-2.0（上游为 Apache-2.0 / LGPL 双许可，本项目按 Apache-2.0 使用） | © 2007-2024 JNA 贡献者 |
| McPacks / FastPacks 1.0 | `com/mcpacks/**`、`assets/mcpacks/**`、`mcpacks.mixins.json`（上游 Forge 1.8.9 模组移植到 Legacy Fabric，已修改；移植版未单独公开发布，仅内置为本客户端资源包管理器） | MIT | © 2025 McPacks Team（Modrinth 所有者 Dulandor），上游 https://modrinth.com/mod/fastpacks |
| Roboto / Material Icons | `assets/md3/font/*.png`、`assets/md3/icon/material_icons.png`（构建期栅格化 + 子集化，已修改；原始 TTF 不分发） | Apache-2.0 | © 2011 Google Inc. / © 2014 Google Inc. |
| Blur（背景模糊） | `com/amethyst/blur/**`（tterrag1098/Blur 1.0.4 的 Legacy Fabric 移植，源文件与 `shaders/post/fade_in_blur.json`、`shaders/program/fade_in_blur.fsh` 逐字引入，未修改模糊算法） | MIT | © 2017 tterrag，上游 https://github.com/tterrag1098/Blur |
| Noto Sans CJK 字形图集 | `assets/md3/font/*.png` 内的 CJK 字形（构建期从 Noto Sans CJK SC 栅格化 + 子集化，已修改；原始字体文件不分发） | SIL OFL 1.1 | © 2012 Google Inc. / © 2014-2021 Adobe |

## 二、许可全文与中文参考译文

| 组件 | 许可全文（英文原文） |
|---|---|
| JNA | `licenses/APACHE-2.0.txt` |
| McPacks / FastPacks 1.0 | `licenses/FASTPACKS-MIT.txt` |
| Blur | `licenses/BLUR-MIT.txt` |
| Roboto / Material Icons | `licenses/APACHE-2.0.txt` |
| Noto Sans / Noto Sans CJK | `licenses/NOTO-OFL-1.1.txt` |
| 本项目自身 | `LICENSE` |

译文仅便于阅读，不具法律效力；与英文原文有歧义时以英文原文为准。

## 三、说明

除上表之外，jar 里没有其它第三方代码或资源。`net.client` 与 `net.client.ui.md3` 包下的 Java 代码（客户端设置界面、HUD/渲染增强、账号、输入法修复、MD3 界面套件，以及 `net.client.replay.*` 回放子系统）均为本项目为本客户端自研（唯一例外是 `net.client.util.BackdropBlurPipeline` 的背景模糊着色器与 pass 流程移植自 Blur，MIT，见上表），从未作为独立项目以任何开源许可对外发布。`com.mcpacks` 为上表所列上游 FastPacks/McPacks 的修改版移植，按 MIT 许可随包分发；该移植版从未作为独立模组公开发布，仅内置为本客户端的资源包管理器。`assets/md3` 图集内烘焙的 Roboto、Material Icons、Noto 字形/图标按上表各自的宽松许可分发。

各类玩法功能（格挡挥刀、受击残影、模组列表、线性鼠标灵敏度、MD3 字体接管等）在 Minecraft 客户端生态中属于通用实现，本项目按自己的结构独立编写，不针对任何具体上游项目做来源声明。

## 四、维护规则

1. 新增任何来自外部的代码或资源，必须先在上表登记，再合入。
2. 只允许合入 **MIT / BSD / Apache-2.0 / OFL / Zlib** 这类宽松许可的内容；GPL / LGPL / 无许可的代码不得进入本仓库。
3. 复用了他人源码或资源的，文件头保留上游版权行，并在 `NOTICE.txt` 增加条目、附许可全文；仅自行实现的通用功能，不写来源声明。
4. `NOTICE.txt`、`licenses/` 与本文件必须与实际 jar 内容一致，发版前核对一次。
5. 新增许可时只放英文原文，不再附带中文参考译文（译文不属于分发内容，也不再随包携带）。
6. `licenses/` 里不得保留与实际 jar 内容不符的过期条目（例如已移除的 JInput、Modern Font Pack）。
