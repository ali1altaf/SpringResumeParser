from flask import Flask, request, jsonify
import os
import google.generativeai as genai

app = Flask(__name__)


#rakesh='good'

# Configure the Gemini API key
genai.configure(api_key='AIzaSyB7WPf4-2TNaNl56EjirmSU9MaeBC-61jo')

# Model configuration
generation_config = {
    "temperature": 1,
    "top_p": 0.95,
    "top_k": 40,
    "max_output_tokens": 8192,
    "response_mime_type": "text/plain",
}

# Initialize the model
model = genai.GenerativeModel(
    model_name="gemini-2.0-flash-exp",
    generation_config=generation_config,
)

@app.route('/extract-summary', methods=['POST'])
def extract_summary():
    # Get JSON payload from the request
    data = request.json
    resume_content = data.get("resume_content")

    if not resume_content:
        return jsonify({"error": "Resume content is missing."}), 400

    try:
        # Use Gemini API to extract the summary and skills
        chat_session = model.start_chat(history=[])
        response_skills = chat_session.send_message(
            f"Extract skills from this resume:\n{resume_content}"
        )

        response_summary = chat_session.send_message(
            f"Extract skills from this resume:\n{resume_content}"
        )

        # Return the extracted summary and placeholder for skills
        return jsonify({
            "summary": response_summary.text,
            "skills": response_skills.text  # Update this if specific skills extraction is implemented
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    # Run the Flask app
    app.run(host='0.0.0.0', port=5001)
