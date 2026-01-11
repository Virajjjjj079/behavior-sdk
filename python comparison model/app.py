# from flask import Flask, request, jsonify
# import joblib
# import numpy as np

# app = Flask(__name__)

# # ---- Load model ----
# try:
#     model = joblib.load('behavior_auth_model.pkl')
#     print("✅ Model loaded successfully.")
# except Exception as e:
#     print("⚠️ Error loading model:", e)
#     model = None

# @app.route('/')
# def home():
#     return jsonify({
#         "message": "Behavior Authentication API is running.",
#         "usage": "Send POST request to /predict with feature data in JSON format."
#     })

# @app.route('/predict', methods=['POST'])
# def predict():
#     if model is None:
#         return jsonify({"error": "Model not loaded"}), 500

#     try:
#         data = request.get_json()
#         required_fields = [
#             'typing_speed', 'avg_key_hold_time', 'avg_flight_time',
#             'total_typing_duration', 'straightness', 'speed_cv', 'idle_ratio',
#             'direction_change_rate'
#         ]
        
#         # Validate input fields
#         if not all(field in data for field in required_fields):
#             return jsonify({
#                 "error": "Missing fields in request body.",
#                 "required_fields": required_fields
#             }), 400

#         # Convert to numpy array
#         features = np.array([[float(data[field]) for field in required_fields]])

#         # Predict — check if model supports probabilities
#         if hasattr(model, "predict_proba"):
#             prob = model.predict_proba(features)[0][1]  # probability of fraud
#             result = 'fraud' if prob > 0.6 else 'genuine'
#             return jsonify({
#                 "probability_of_fraud": round(float(prob), 3),
#                 "result": result
#             })
#         else:
#             prediction = model.predict(features)[0]
#             result = 'fraud' if prediction == 1 else 'genuine'
#             return jsonify({"result": result})

#     except Exception as e:
#         return jsonify({"error": str(e)}), 500



# required_fields = [
#     'typing_speed', 'avg_key_hold_time', 'avg_flight_time',
#     'total_typing_duration', 'straightness', 'speed_cv', 'idle_ratio',
#     'direction_change_rate'
# ]

#         # ✅ New endpoint to compare two users
# @app.route('/compare', methods=['POST'])
# def compare_users():
#     try:
#         data = request.get_json()

#         # Validate input
#         if 'user1' not in data or 'user2' not in data:
#             return jsonify({"error": "Request must include 'user1' and 'user2'"}), 400

#         user1 = data['user1']
#         user2 = data['user2']

#         if not all(field in user1 for field in required_fields) or not all(field in user2 for field in required_fields):
#             return jsonify({
#                 "error": "Both users must have all required fields.",
#                 "required_fields": required_fields
#             }), 400

#         # Compute absolute difference
#         diff = {field: abs(float(user1[field]) - float(user2[field])) for field in required_fields}

#         return jsonify(diff)

#     except Exception as e:
#         return jsonify({"error": str(e)}), 500




        

# if __name__ == '__main__':
#     app.run(host='0.0.0.0', port=5000, debug=True)



from flask import Flask, request, jsonify
import joblib
import numpy as np
from math import radians, sin, cos, sqrt, atan2
from datetime import datetime, timedelta

app = Flask(__name__)

# ---- Load model ----
try:
    model = joblib.load('behavior_auth_model.pkl')
    print("✅ Model loaded successfully.")
except Exception as e:
    print("⚠️ Error loading model:", e)
    model = None

@app.route('/')
def home():
    return jsonify({
        "message": "Behavior Authentication API is running.",
        "usage": "Send POST request to /predict, /compare, or /location-check"
    })


# -------------------------------------------
#      🧠 Existing Behavior Auth Prediction
# -------------------------------------------
@app.route('/predict', methods=['POST'])
def predict():
    if model is None:
        return jsonify({"error": "Model not loaded"}), 500

    try:
        data = request.get_json()

        required_fields = [
            'typing_speed', 'avg_key_hold_time', 'avg_flight_time',
            'total_typing_duration', 'straightness', 'speed_cv', 'idle_ratio',
            'direction_change_rate'
        ]

        if not all(field in data for field in required_fields):
            return jsonify({
                "error": "Missing fields.",
                "required_fields": required_fields
            }), 400

        features = np.array([[float(data[field]) for field in required_fields]])

        if hasattr(model, "predict_proba"):
            prob = model.predict_proba(features)[0][1]
            result = "fraud" if prob > 0.6 else "genuine"

            return jsonify({
                "probability_of_fraud": round(float(prob), 3),
                "result": result
            })
        else:
            prediction = model.predict(features)[0]
            result = "fraud" if prediction == 1 else "genuine"

            return jsonify({"result": result})
    except Exception as e:
        return jsonify({"error": str(e)}), 500



# # -------------------------------------------
# #      🔍 Compare User Feature Differences
# # -------------------------------------------
# required_fields = [
#     'typing_speed', 'avg_key_hold_time', 'avg_flight_time',
#     'total_typing_duration', 'straightness', 'speed_cv', 'idle_ratio',
#     'direction_change_rate'
# ]

# @app.route('/compare', methods=['POST'])
# def compare_users():
#     try:
#         data = request.get_json()

#         if 'user1' not in data or 'user2' not in data:
#             return jsonify({"error": "Include both user1 and user2"}), 400

#         user1, user2 = data['user1'], data['user2']

#         if not all(field in user1 for field in required_fields) or not all(field in user2 for field in required_fields):
#             return jsonify({
#                 "error": "Both users must have all required fields.",
#                 "required_fields": required_fields
#             }), 400

#         diff = {field: abs(float(user1[field]) - float(user2[field])) for field in required_fields}

#         return jsonify(diff)

#     except Exception as e:
#         return jsonify({"error": str(e)}), 500


# -------------------------------------------
#      🔍 Compare + Predict Combined
# -------------------------------------------
required_fields = [
    'typingSpeed', 'avgKeyHoldTime', 'avgFlightTime',
    'totalTypingDuration', 'straightness', 'speedCv', 'idleRatio',
    'directionChangeRate'
]

@app.route('/compare', methods=['POST'])
def compare_users():
    try:
        data = request.get_json()

        if 'user1' not in data or 'user2' not in data:
            return jsonify({"error": "Include both user1 and user2"}), 400

        user1 = data['user1']   # BASE
        user2 = data['user2']   # TEST (will be used for fraud prediction)

        # -------------------------------
        # VALIDATION
        # -------------------------------
        if not all(field in user1 for field in required_fields) or not all(field in user2 for field in required_fields):
            return jsonify({
                "error": "Both users must have all required fields.",
                "required_fields": required_fields
            }), 400

        # -------------------------------
        # 1️⃣ DIFFERENCES
        # -------------------------------
        differences = {
            field: abs(float(user1[field]) - float(user2[field]))
            for field in required_fields
        }

        # -------------------------------
        # 2️⃣ PREDICTION LOGIC (same as /predict)
        # -------------------------------
        features = np.array([[float(user2[field]) for field in required_fields]])

        if hasattr(model, "predict_proba"):
            prob = model.predict_proba(features)[0][1]
            result = "fraud" if prob > 0.6 else "genuine"

            prediction_output = {
                "probability_of_fraud": round(float(prob), 3),
                "result": result
            }

        else:
            prediction = model.predict(features)[0]
            result = "fraud" if prediction == 1 else "genuine"
            prediction_output = {"result": result}

        # -------------------------------
        # 3️⃣ FINAL RESPONSE
        # -------------------------------
        return jsonify({
            "differences": differences,
            "prediction": prediction_output
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500




# -------------------------------------------
#        🌍 NEW LOCATION CHECK ENDPOINT
# -------------------------------------------
def haversine(lat1, lon1, lat2, lon2):
    R = 6371  # Earth radius in km
    dlat = radians(lat2 - lat1)
    dlon = radians(lon2 - lon1)
    a = sin(dlat/2)**2 + cos(radians(lat1)) * cos(radians(lat2)) * sin(dlon/2)**2
    c = 2 * atan2(sqrt(a), sqrt(1-a))
    return R * c  # Distance in km


@app.route('/location-check', methods=['POST'])
def location_check():
    try:
        data = request.get_json()

        required = ["lat1", "lon1", "lat2", "lon2", "time1", "time2"]
        if not all(f in data for f in required):
            return jsonify({"error": "Missing required fields", "required": required}), 400

        lat1 = float(data["lat1"])
        lon1 = float(data["lon1"])
        lat2 = float(data["lat2"])
        lon2 = float(data["lon2"])

        # Parse ISO timestamps
        t1 = datetime.fromisoformat(data["time1"])
        t2 = datetime.fromisoformat(data["time2"])

        # -----------------------------
        # Ensure t2 is always AFTER t1
        # -----------------------------
        if t2 <= t1:
            t2 = t2 + timedelta(days=1)

        time_diff_hours = (t2 - t1).total_seconds() / 3600

        if time_diff_hours <= 0:
            return jsonify({"error": "Invalid time difference"}), 400

        distance_km = haversine(lat1, lon1, lat2, lon2)
        required_speed = distance_km / time_diff_hours

        # Speed thresholds (km/h)
        max_plane = 900  # highest realistic speed

        # Decision
        if required_speed > max_plane:
            decision = 1
            decision_text = "fraud"
        else:
            decision = 0
            decision_text = "genuine"

        return jsonify({
            "distance_km": round(distance_km, 2),
            "time_hours": round(time_diff_hours, 2),
            "required_speed_kmh": round(required_speed, 2),
            "allowed_speed_limit": max_plane,
            "numeric_output": decision,
            "result": decision_text
        })

   

    except Exception as e:
        return jsonify({"error": str(e)}), 500
    


    #DEVICEFINGERPRINT COMPARE


WEIGHTS = {
    "userAgent": 0.25,
    "platform": 0.2,
    "screenResolution": 0.15,
    "fonts": 0.1,
    "plugins": 0.1,
    "timezone": 0.1,
    "language": 0.05,
    "colorDepth": 0.05
}

THRESHOLD = 0.8  # same-device confidence

@app.route("/device-compare", methods=["POST"])
def compare_device():
    data = request.get_json()

    base = data.get("base", {})
    test = data.get("test", {})

    score = 0.0
    differences = {}

    for field, weight in WEIGHTS.items():
        base_val = base.get(field)
        test_val = test.get(field)

        match = compare_field(field, base_val, test_val)

        if match:
            score += weight
        else:
            differences[field] = {
                "base": base_val,
                "test": test_val
            }

    confidence = round(score, 2)

    result = (
        "genuine_same_device"
        if confidence >= THRESHOLD
        else "new_device_detected"
    )

    return jsonify({
        "result": result,
        "confidence": confidence,
        "differences": differences
    })


# ----------------- Comparison helpers -----------------

# def compare_field(field, base, test):
#     if base is None or test is None:
#         return False

#     if field == "userAgent":
#         return extract_os_browser(base) == extract_os_browser(test)

#     if field in ("plugins", "fonts"):
#         return normalize_list(base) == normalize_list(test)

#     return str(base).strip().lower() == str(test).strip().lower()


# def extract_os_browser(ua):
#     """
#     Extract OS + browser family only
#     """
#     ua = ua.lower()

#     os = "unknown"
#     if "windows" in ua:
#         os = "windows"
#     elif "android" in ua:
#         os = "android"
#     elif "iphone" in ua or "ios" in ua:
#         os = "ios"
#     elif "mac os" in ua:
#         os = "macos"
#     elif "linux" in ua:
#         os = "linux"

#     browser = "unknown"
#     if "chrome" in ua and "safari" in ua:
#         browser = "chrome"
#     elif "safari" in ua and "chrome" not in ua:
#         browser = "safari"
#     elif "firefox" in ua:
#         browser = "firefox"
#     elif "edge" in ua:
#         browser = "edge"

#     return f"{os}:{browser}"


# def normalize_list(value):
#     if not value:
#         return set()
#     return set(v.strip().lower() for v in value.split(","))



from flask import Flask, request, jsonify
import re
from typing import Dict, Tuple

app = Flask(__name__)

# =========================
# CONFIGURATION
# =========================

FIELD_WEIGHTS = {
    "userAgent": 0.30,
    "platform": 0.15,
    "timezone": 0.10,
    "language": 0.10,
    "fonts": 0.10,
    "plugins": 0.10,
    "screenResolution": 0.10,
    "colorDepth": 0.05,
}

MATCH_THRESHOLD = 0.85
OVERLAP_THRESHOLD = 0.6
RESOLUTION_AREA_THRESHOLD = 0.5


# =========================
# NORMALIZERS
# =========================

def normalize_str(value: str) -> str:
    return str(value).strip().lower()


def normalize_list(value: str) -> set:
    return set(
        normalize_str(v)
        for v in re.split(r",\s*", value or "")
        if v.strip()
    )


# =========================
# USER AGENT PARSING
# =========================

def extract_os_browser(ua: str) -> Tuple[str, str]:
    ua = ua.lower()

    if "windows nt" in ua:
        os = "windows"
    elif "mac os x" in ua:
        os = "macos"
    elif "android" in ua:
        os = "android"
    elif "iphone" in ua or "ipad" in ua:
        os = "ios"
    elif "linux" in ua:
        os = "linux"
    else:
        os = "unknown"

    if "chrome" in ua and "edg" not in ua:
        browser = "chrome"
    elif "edg" in ua:
        browser = "edge"
    elif "firefox" in ua:
        browser = "firefox"
    elif "safari" in ua and "chrome" not in ua:
        browser = "safari"
    else:
        browser = "unknown"

    return os, browser


# =========================
# FIELD COMPARATORS
# =========================

def overlap_match(base: str, test: str, threshold=OVERLAP_THRESHOLD) -> bool:
    base_set = normalize_list(base)
    test_set = normalize_list(test)

    if not base_set or not test_set:
        return False

    overlap = len(base_set & test_set) / max(len(base_set), len(test_set))
    return overlap >= threshold


def resolution_match(r1: str, r2: str) -> bool:
    try:
        w1, h1 = map(int, r1.lower().split("x"))
        w2, h2 = map(int, r2.lower().split("x"))
    except:
        return False

    area1 = w1 * h1
    area2 = w2 * h2
    ratio = min(area1, area2) / max(area1, area2)

    return ratio >= RESOLUTION_AREA_THRESHOLD


def compare_field(field: str, base, test) -> bool:
    if base is None or test is None:
        return False

    if field == "userAgent":
        return extract_os_browser(base) == extract_os_browser(test)

    if field in ("fonts", "plugins"):
        return overlap_match(base, test)

    if field == "screenResolution":
        return resolution_match(base, test)

    return normalize_str(base) == normalize_str(test)


# =========================
# CORE MATCH FUNCTION
# =========================

def match_device(base_fp: Dict, test_fp: Dict) -> Dict:
    score = 0.0
    matched_fields = []
    mismatched_fields = []

    for field, weight in FIELD_WEIGHTS.items():
        if compare_field(field, base_fp.get(field), test_fp.get(field)):
            score += weight
            matched_fields.append(field)
        else:
            mismatched_fields.append(field)

    confidence = round(score, 2)

    result = (
        "genuine_same_device"
        if confidence >= MATCH_THRESHOLD
        else "new_device_detected"
    )

    return {
        "result": result,
        "confidence": confidence,
        "matched": matched_fields,
        "mismatched": mismatched_fields
    }


# =========================
# 🚀 API ENDPOINT
# =========================

@app.route("/device-compare", methods=["POST"])
def compare_device():
    """
    Expected JSON:
    {
      "base": { ...device fingerprint... },
      "test": { ...device fingerprint... }
    }
    """

    data = request.get_json()

    if not data or "base" not in data or "test" not in data:
        return jsonify({
            "error": "Invalid request. Provide 'base' and 'test' fingerprints."
        }), 400

    result = match_device(data["base"], data["test"])

    return jsonify(result), 200


# =========================
# APP START
# =========================




if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)















