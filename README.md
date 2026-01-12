# Boyfriend Helper Agent

这是一个基于 Spring Boot 3.x、JDK 17 和 Spring AI Alibaba (DashScope) 构建的智能 Agent 应用。
它的主要功能是帮助"不会拍照的男朋友"分析照片，并提供具体的改进建议。

## 技术栈

- **Spring Boot**: 3.2.5
- **JDK**: 17+
- **Spring AI Alibaba**: 1.0.0-M1 (基于 Alibaba Cloud DashScope)
- **Model**: Qwen-VL-Max (通义千问视觉大模型)

## 快速开始

### 1. 前置要求

- JDK 17 或更高版本
- Maven 3.x
- 阿里云 DashScope API Key (需要开通通义千问服务)

### 2. 配置 API Key

你需要设置环境变量 `DASHSCOPE_API_KEY`，或者直接在 `src/main/resources/application.yml` 中修改（不推荐提交到版本控制）。

**MacOS / Linux:**
```bash
export DASHSCOPE_API_KEY=sk-your-actual-api-key
```

**Windows (PowerShell):**
```powershell
$env:DASHSCOPE_API_KEY="sk-your-actual-api-key"
```

### 3. 构建项目

如果遇到依赖下载问题，请确保你的 Maven 配置允许访问 Spring Milestones 仓库，或者使用了阿里云的 Spring 代理仓库。

```bash
mvn clean install
```

### 4. 运行应用

```bash
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## 接口文档

### 照片分析接口

- **URL**: `/api/analyze`
- **Method**: `POST`
- **Content-Type**: `multipart/form-data`
- **Parameters**:
  - `file`: (Required) 上传的图片文件

**cURL 示例:**

```bash
curl -X POST -F "file=@/path/to/your/photo.jpg" http://localhost:8080/api/analyze
```

**响应示例:**

```text
构图：这张照片采用了中心构图...
光线：光线有点背光，建议...
角度：俯拍的角度让女朋友显得...

建议：
1. 尝试放低手机镜头...
2. 寻找侧顺光...
3. 注意背景杂乱...
```

## 注意事项

- 本项目使用了 Spring AI Alibaba 的 Milestone 版本 (1.0.0-M1)，请确保网络环境可以访问相关 Maven 仓库。
- 如果构建失败提示找不到 `spring-ai-alibaba-starter`，请尝试在 Maven settings 中添加阿里云 Spring 代理或检查网络。
