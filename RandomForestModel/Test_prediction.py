import os
import numpy as np
import joblib
from sklearn.ensemble import RandomForestClassifier

# 反向标签映射表，模型预测输出的标签将转化为对应的文件类型
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

# 从文件中提取特征的函数
def extract_features(file_path):
    try:
        with open(file_path, 'rb') as file:
            data = file.read()
        # 提取文件大小
        file_size = len(data)
        # 提取文件的字节分布（0-255的频率）
        byte_distribution = np.bincount(np.frombuffer(data, dtype=np.uint8), minlength=256)
        # 返回文件大小和字节分布作为特征向量
        return np.concatenate([[file_size], byte_distribution])
    except Exception as e:
        print(f"Error processing file {file_path}: {e}")
        return None

# 模型预测函数
def predict_file_type(model_path, file_path):
    # 加载训练好的随机森林模型
    model = joblib.load(model_path)

    # 提取文件特征
    features = extract_features(file_path)

    # 如果特征提取成功，则进行预测
    if features is not None:
        # 进行文件类型的预测
        predicted_label = model.predict([features])[0]
        predicted_file_type = label_map[predicted_label]
        print(f"Predicted file type for {file_path}: {predicted_file_type}")
        return predicted_file_type
    else:
        print("Could not extract features from the file.")
        return None

# 主函数，输入文件路径和模型路径，进行预测
if __name__ == "__main__":
    # 模型的路径
    model_path = "random_forest_file_classification_model_50.pkl"
    # 需要预测的文件路径
    file_path = "/Users/xiaofuqing/PycharmProjects/RandomForestModel/data/gif/file_2.gif"  # 替换为实际文件路径

    # 进行文件类型预测
    predict_file_type(model_path, file_path)