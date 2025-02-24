from flask import Flask, request, jsonify  # Import Flask for creating the web app and handling HTTP requests/responses
import google.generativeai as genai  # Import the Gemini Generative AI library for using generative AI models

# Initialize a Flask application
app = Flask(__name__)

@app.route('/extract-summary', methods=['POST'])
def extract_summary():
    """
    Endpoint to extract a professional summary and skills from a resume using Generative AI.
    """

    # Get JSON payload from the request
    data = request.json
    resume_content = data.get("resume_content")  # Extract the resume content from the request JSON
    gemini_api_key = data.get("api_key")  # Extract the Gemini API key from the request JSON
    job_description = data.get("job_description")  # Extract the job description from the request JSON

    # Check if required fields are provided in the request
    if not resume_content:
        return jsonify({"error": "Resume content is missing."}), 400

    if not gemini_api_key:
        return jsonify({"error": "API key is missing."}), 400

    # Configure the Gemini API key
    genai.configure(api_key=gemini_api_key)

    # Define model configuration settings
    generation_config = {
        "temperature": 1,  # Controls randomness in the output; higher values generate more diverse text
        "top_p": 0.95,  # Nucleus sampling parameter; controls the cumulative probability of token choices
        "top_k": 40,  # Limits token sampling to the top-k tokens, reducing randomness
        "max_output_tokens": 8192,  # Maximum number of tokens allowed in the output
        "response_mime_type": "text/plain",  # Expected response format from the model
    }

    # Initialize the generative model with the specified configuration
    model = genai.GenerativeModel(
        model_name="gemini-2.0-flash-exp",  # Specify the Gemini model version
        generation_config=generation_config,
    )

    try:
        # Create a new chat session for interacting with the model
        chat_session = model.start_chat(history=[])

        # Generate skills categorized into 'Technical Skills,' 'Soft Skills,' and 'Domain-Specific Skills'
        response_skills = chat_session.send_message(
            f"You are an AI designed to analyze resumes and extract relevant professional skills in 50 words,result should be comma separated skills. "
            f"From the following resume content, identify the technical, soft, and domain-specific skills explicitly mentioned. "
            f"Provide the skills as a categorized list under these headers: 'Technical Skills,' 'Soft Skills,' and 'Domain-Specific Skills.' "
            f"Only include items that are clearly identifiable as skills, avoiding redundant phrases or generic terms. "
            f"Resume Content: \n{resume_content}"
        )

        # Generate a professional summary from the resume content
        response_summary = chat_session.send_message(
            f"You are an AI designed to create professional summaries from resumes. "
            f"Using the following resume content, generate a concise, high-quality summary within 200 words. "
            f"Focus on the candidate’s key accomplishments, professional experience, skills, and expertise. "
            f"Avoid generic phrases and ensure the summary highlights the most impactful and relevant information about the candidate. "
            f"The summary should read fluently and professionally, suitable for use in a job application or professional profile. "
            f"Resume Content:\n{resume_content}"
        )

        response_ats = ""

        try:
            if job_description and job_description.strip():  # Ensuring job description is not empty
                response_ats = chat_session.send_message(
                    f"You are an AI specialized in resume analysis and Applicant Tracking System (ATS) scoring. "
                    f"Your task is to evaluate how well a given resume aligns with a job description by analyzing "
                    f"skill relevance, experience match, keyword alignment, and overall job fit "
                    f"Given a job description and a resume text, analyze how well the resume aligns with the job requirements. "
                    f"Provide a matchability score between 0 and 100, considering factors like skill relevance, experience match, keyword alignment, and overall job fit. "
                    f"Also, highlight key missing skills or qualifications. Ensure accuracy in evaluation and provide actionable feedback. "
                    f"Here is the job description:\n{job_description.strip()}\n\n"
                    f"Here is the resume content:\n{resume_content.strip()}\n\n"
                    f"Return the response in the following HashMap format as a string (max 2999 characters):\n"
                    f"{{\n"
                    f'  "job_description": put job description value as 1,\n'
                    f'  "ATS_score": " Quantify matchability based on alignment with key job requirements  give a score between (0-100)",\n'
                    f'  "resume_strengths": "Identify skills, experiences, and keywords that match the job description.",\n'
                    f'  "resume_weaknesses": "Highlight missing skills, qualifications, or areas lacking emphasis.",\n'
                    f'  "important_resume_qualities": "brief explanation of the scoring rationale.",\n'
                    f'  "Suggested_Resume_Improvements": "Suggest specific ways to optimize the resume, such '
                    f'as adding missing keywords, highlighting relevant projects, restructuring sections, or '
                    f'gaining additional certifications. '
                    f'Provide actionable recommendations to enhance the resume’s ATS compatibility'
                    f'Format the output as points with numbered line breaks where required."\n'
                    f"}}\n"
                )
            else:
                response_ats = chat_session.send_message(
                    f"You are an AI specialized in resume analysis and Applicant Tracking System (ATS) scoring. "
                    f"Given a resume text, evaluate it based on key aspects of an effective resume, considering factors such as skill relevance based on the "
                    f"relevant industry (derived from work experience and projects), experience clarity, keyword optimization, readability, and ATS compatibility. "
                    f"Provide an ATS score between 0 and 100, reflecting how well the resume is structured for ATS systems. "
                    f"Identify key strengths, highlight important elements of a strong resume, and suggest specific improvements to enhance its effectiveness. "
                    f"Ensure accuracy in evaluation and provide actionable feedback. "
                    f"Here is the resume content:\n{resume_content.strip()}\n\n"
                    f"Return the response in the following HashMap format as a string (max 2999 characters):\n"
                    f"{{\n"
                    f'  "job_description": put job description value as 0,\n'
                    f'  "ATS_score": "integer (0-100)",\n'
                    f'  "resume_strengths": "highlighted strengths in skills, formatting, achievements, and clarity",\n'
                    f'  "important_resume_qualities": "key elements of a good resume, such as readability, keyword optimization, structured sections, and quantified achievements",\n'
                    f'  "resume_weaknesses": "areas needing improvement, such as missing skills, vague descriptions, weak formatting, or lack of impact-driven statements",\n'
                    f'  "Suggested_Resume_Improvements": "Suggest specific ways to optimize the resume, such '
                    f'as adding industry-relevant keywords, emphasizing measurable achievements, restructuring sections for clarity, '
                    f'or acquiring additional certifications. Format the output as points with numbered line breaks where required."\n'
                    f"}}\n"
                )

            # Ensure the response is valid and not empty
            ats_result = response_ats.text.strip() if response_ats and response_ats.text.strip() else "N/A"

        except Exception as e:
            ats_result = f"Error: {str(e)}"  # Handle unexpected AI response failures


        # Final result dictionary
        result = {
            "summary": response_summary.text.strip() if response_summary else "N/A",
            "skills": response_skills.text.strip() if response_skills else "N/A",
            "ats_match": ats_result
        }

        # Return the generated summary, skills, and (if available) ATS matchability score
        return jsonify(result)

    except Exception as e:
        # Return an error response with the exception message if something goes wrong
        return jsonify({"error": str(e)}), 500

if __name__ == '__main__':
    # Run the Flask app on all network interfaces (host='0.0.0.0') at port 5001
    app.run(host='0.0.0.0', port=5001)
