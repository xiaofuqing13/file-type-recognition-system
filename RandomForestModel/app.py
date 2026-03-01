import os
import joblib
import numpy as np
import filetype
import json
from flask import Flask, request, jsonify

# 初始化 Flask 应用
app = Flask(__name__)

# 加载训练好的随机森林模型
model = joblib.load(os.path.join(os.path.dirname(__file__), 'random_forest_file_classification_model_50.pkl'))

# 文件标签映射
label_map = {
    0: '.txt', 1: '.json', 2: '.py', 3: '.png', 4: '.docx',
    5: '.csv', 6: '.html', 7: '.xml', 8: '.jpg', 9: '.gif',
    10: '.pdf', 11: '.xls', 12: '.xlsx', 13: '.ppt', 14: '.pptx',
    15: '.zip', 16: '.tar', 17: '.rar', 18: '.exe', 19: '.bat',
    20: '.sh', 21: '.mp3', 22: '.wav', 23: '.mp4', 24: '.avi',
    25: '.mov', 26: '.mkv', 27: '.flv', 28: '.iso', 29: '.bin',
    30: '.rtf', 31: '.md', 32: '.yml', 33: '.ini', 34: '.log',
    35: '.c', 36: '.cpp', 37: '.java', 38: '.class', 39: '.js',
    40: '.ts', 41: '.css', 42: '.scss', 43: '.go', 44: '.rb',
    45: '.php', 46: '.swift', 47: '.kt', 48: '.dart', 49: '.h'
}

# 提取文件特征
def extract_features(file_path):
    try:
        with open(file_path, 'rb') as file:
            data = file.read()
        file_size = len(data)
        byte_distribution = np.bincount(np.frombuffer(data, dtype=np.uint8), minlength=256)
        return np.concatenate([[file_size], byte_distribution])
    except Exception as e:
        print(f"Error processing file {file_path}: {e}")
        return None

# 预测文件类型
def predict_file_type(file_path):
    features = extract_features(file_path)
    if features is None:
        return 'unknown'
    predicted_label = model.predict([features])[0]
    return label_map.get(predicted_label, 'unknown')

# 使用 filetype 库检测文件实际类型，并在 filetype 无法识别时处理为文本文件
def detect_actual_file_type(file_path):
    kind = filetype.guess(file_path)
    if kind is None:
        # 使用文件扩展名作为备选方案
        _, file_extension = os.path.splitext(file_path)
        if file_extension in ['.txt', '.json', '.py', '.md', '.csv']:
            return file_extension  # 假设这些文件是文本文件
        else:
            return 'unknown'
    return f".{kind.extension}"

# 定义 Flask 路由来处理 Java 的请求
@app.route('/process_file', methods=['POST'])
def classify_file():
    # 获取上传的文件
    if 'file' not in request.files:
        print(request.files)
        return jsonify({"error": "No file provided"}), 400

    file = request.files['file']

    # 将文件保存到临时目录
    temp_dir = os.path.join(os.getcwd(), 'temp')
    if not os.path.exists(temp_dir):
        os.makedirs(temp_dir)

    file_path = os.path.join(temp_dir, file.filename)
    file.save(file_path)

    # 使用随机森林模型进行文件类型预测
    predicted_type = predict_file_type(file_path)

    # 使用 filetype 库检测文件实际类型
    actual_type = detect_actual_file_type(file_path)

    # 比较预测类型和实际类型
    comparison_result = 1 if predicted_type == actual_type else 0

    # 准备返回给 Java 的 JSON 响应
    response = {
        "predicted-type": predicted_type,
        "actual-type": actual_type,
        "comparison-result": comparison_result
    }

    # 返回 JSON 响应
    return jsonify(response)

# 启动 Flask 应用
if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5001)