const urlParams = new URLSearchParams(window.location.search);
    const fileId = urlParams.get('id');
    const resumeContentDiv = document.getElementById('resumeContent');
    const atsScoreDiv = document.getElementById('atsScore');

    if (!fileId) {
        resumeContentDiv.innerHTML = '<p class="error-message">Error: Invalid or missing file ID.</p>';
        atsScoreDiv.textContent = "N/A";
    } else {
        fetch(`/api/resumes/summary/${fileId}`)
            .then(response => response.json())
            .then(data => {
                if (data.error) {
                    resumeContentDiv.innerHTML = `<p class="error-message">Error: ${data.error}</p>`;
                    atsScoreDiv.textContent = "N/A";
                } else {
                    const skillsFormatted = data.skills
                        ? data.skills.replace(/\*\*(.*?)\*\*/g, '<b>$1</b>').replace(/\. /g, '.<br>')
                        : "No skills available.";

                    resumeContentDiv.innerHTML = `
                        <h4>Skills:</h4>
                        <p>${skillsFormatted}</p>
                        <h4>Summary:</h4>
                        <p>${data.summary || "No summary available."}</p>
                    `;

                    atsScoreDiv.innerHTML = `
                        <h4>ATS Score:</h4>
                        <p>${data.ats_score}</p>
                        <h4>Summary:</h4>
                        <p>${data.ats_imp_qualities || "No summary available."}</p>
                        <h4>Resume Weakness / Missing Skills:</h4>
                        <p>${data.resume_weakness || "No resume weakness available."}</p>
                        <h4>Resume Strengths / Matching Skills:</h4>
                        <p>${data.resume_strengths}</p>
                        <h4>Suggested Resume Improvements:</h4>
                        <p>${data.Suggested_Resume_Improvements || "No Suggestions."}</p>
                    `;
                }
            })
            .catch(error => {
                resumeContentDiv.innerHTML = `<p class="error-message">Error fetching resume details: ${error.message}</p>`;
                atsScoreDiv.textContent = "N/A";
            });
    }