# File Type Recognition System

基于 **随机森林（Random Forest）** 机器学习算法的文件类型智能识别系统，支持 **50 种文件格式** 的自动分类。采用前后端分离架构：Java SpringBoot 后端 + Vue 前端 + Python Flask ML 服务。

## 项目目的

通过分析文件二进制字节分布特征（而非依赖文件扩展名），实现文件真实类型的自动识别。适用于文件格式校验、安全检测、数据管理等场景。

## 核心功能

- **50 种文件类型识别**：覆盖文档（.txt/.docx/.pdf/.xlsx）、代码（.py/.java/.js/.go）、图片（.png/.jpg/.gif）、音视频（.mp3/.mp4/.avi）、压缩包（.zip/.tar/.rar）等
- **字节分布特征提取**：读取文件二进制内容，计算 0-255 字节频率分布（257 维特征向量）
- **随机森林分类器**：100 棵决策树集成预测，通过交叉验证评估
- **双重验证机制**：随机森林预测 + filetype 库检测，对比两种方法的结果
- **自动数据集生成**：可自动生成 50 种类型 × 50 个样本的训练数据集
- **前后端分离架构**：Java 后端处理业务逻辑，Python Flask 提供 ML 推理服务，Vue 前端展示

## 技术架构

```
Vue 前端 (file-type-front/)
    ↓ HTTP
Java SpringBoot 后端 (demo/)
    ↓ REST API (port 5001)
Python Flask ML 服务 (RandomForestModel/)
    ↓
随机森林分类器 → 文件类型预测
    ↓
filetype 库 → 真实类型检测
    ↓
对比结果返回前端
```

## 使用说明

### 1. 训练模型（Python）

```bash
cd RandomForestModel

# 生成训练数据集（可选，如已有数据可跳过）
python Randomly_generate_data_sets.py

# 训练随机森林模型
python model_training.py
```

### 2. 启动 ML 服务（Python）

```bash
cd RandomForestModel
pip install flask numpy joblib scikit-learn filetype
python app.py  # 启动在 port 5001
```

### 3. 启动后端（Java）

```bash
cd demo
mvn spring-boot:run
```

### 4. 启动前端（Vue）

```bash
cd file-type-front
npm install
npm run serve
```

## 适用场景

- 文件上传安全校验（防止伪装扩展名）
- 数字取证 / 文件格式鉴别
- 数据管理系统中的自动分类
- ML 文件分类方法实践

## 技术栈

| 模块 | 技术 |
|------|------|
| ML 模型 | scikit-learn (RandomForestClassifier) |
| ML 服务 | Flask (Python) |
| 后端 | Java SpringBoot + Maven |
| 前端 | Vue.js |
| 文件检测 | filetype (Python) |
| 序列化 | joblib |

## 项目结构

```
.
├── RandomForestModel/           # Python ML 模块
│   ├── app.py                   # Flask API 服务
│   ├── model_training.py        # 模型训练脚本
│   ├── Randomly_generate_data_sets.py  # 训练数据生成器
│   ├── Test_prediction.py       # 预测测试脚本
│   └── complex_text.txt         # 模板文本
├── demo/                        # Java SpringBoot 后端
│   ├── pom.xml
│   └── src/
├── file-type-front/             # Vue 前端
│   ├── package.json
│   ├── src/
│   └── public/
└── README.md
```

## License

MIT License
