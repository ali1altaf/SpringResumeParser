from flask import Flask, request, jsonify
import os
import google.generativeai as genai

app = Flask(__name__)




@app.route('/extract-summary', methods=['POST'])
def extract_summary():

    # Get JSON payload from the request
    data = request.json
    resume_content = data.get("resume_content")

    gemini_api_key = data.get("api_key")

    # Configure the Gemini API key
    genai.configure(api_key=gemini_api_key)

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

    if not resume_content:
        return jsonify({"error": "Resume content is missing."}), 400

    try:
        # Use Gemini API to extract the summary and skills
        chat_session = model.start_chat(history=[])
        response_skills = chat_session.send_message(
            f"You are an AI designed to analyze resumes and extract relevant professional skills in 25 words. From the following resume content, identify the technical, soft, and domain-specific skills explicitly mentioned. Provide the skills as a categorized list under these headers: 'Technical Skills,' 'Soft Skills,' and 'Domain-Specific Skills.' Only include items that are clearly identifiable as skills, avoiding redundant phrases or generic terms. Resume Content: \n{resume_content}"
        )

        response_summary = chat_session.send_message(
            f"You are an AI designed to create professional summaries from resumes. Using the following resume content, generate a concise, high-quality summary within 200 words. Focus on the candidate’s key accomplishments, professional experience, skills, and expertise. Avoid generic phrases and ensure the summary highlights the most impactful and relevant information about the candidate. The summary should read fluently and professionally, suitable for use in a job application or professional profile. Resume Content:\n{resume_content}"
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
