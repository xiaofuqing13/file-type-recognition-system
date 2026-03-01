import os
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score, classification_report
import joblib

# 标签映射，映射50种文件类型
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

# 反向映射，预测时使用
reverse_label_map = {v: k for k, v in label_map.items()}

# 特征提取函数
def extract_features(file_path):
    try:
        with open(file_path, 'rb') as file:
            data = file.read()
        # 提取文件大小
        file_size = len(data)
        # 提取文件中每个字节的分布（0-255的频率）
        byte_distribution = np.bincount(np.frombuffer(data, dtype=np.uint8), minlength=256)
        # 返回文件大小和字节分布
        return np.concatenate([[file_size], byte_distribution])
    except Exception as e:
        print(f"Error processing file {file_path}: {e}")
        return None

# 构建数据集
def build_dataset(folder_path):
    features = []
    labels = []
    for root, dirs, files in os.walk(folder_path):
        for file in files:
            file_path = os.path.join(root, file)
            file_extension = os.path.splitext(file)[1].lower()  # 获取文件扩展名
            if file_extension in reverse_label_map:
                feature = extract_features(file_path)
                if feature is not None:
                    features.append(feature)
                    labels.append(reverse_label_map[file_extension])
    return np.array(features), np.array(labels)

# 训练随机森林模型
def train_random_forest(X_train, y_train):
    rf_model = RandomForestClassifier(n_estimators=100, random_state=42)
    rf_model.fit(X_train, y_train)
    return rf_model

# 主程序
if __name__ == "__main__":
    # 假设数据集位于某个文件夹下，文件夹包含50种文件类型，每种50个文件
    folder_path = os.path.join(os.path.dirname(__file__), "data")  # 数据集路径

    # 构建数据集
    X, y = build_dataset(folder_path)

    # 如果数据集为空，输出错误提示
    if X.size == 0 or y.size == 0:
        print("No valid data found in the dataset.")
    else:
        # 切分训练集和测试集
        X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

        # 训练随机森林模型
        model = train_random_forest(X_train, y_train)

        # 预测并评估模型
        y_pred = model.predict(X_test)
        accuracy = accuracy_score(y_test, y_pred)
        print(f"Test Accuracy: {accuracy:.4f}")

        # 动态生成 target_names
        unique_labels = np.unique(y_test)
        actual_target_names = [label_map[i] for i in unique_labels]

        # 打印分类报告
        print(classification_report(y_test, y_pred, target_names=actual_target_names, zero_division=0))

        # 保存模型
        joblib.dump(model, 'random_forest_file_classification_model_50.pkl')