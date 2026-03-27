import axios from "axios";
 
export const generateAIResponse = async (formData) => {

  const now = new Date();

  let year = now.getFullYear();
 
 
  if (formData.practiceName !== null || formData.practiceName !== undefined) {
 
    try {
 
      let comprehensivePrompt = `## Context ##

EPAM India provides digital products and platforms development, cloud transformation, data and analytics, quality engineering and technology transformation services to world-wide clients leveraging global EPAM strengths and engineering DNA.
EPAM serve clients from various domanis such as Software & Hi-Tech, Life Sciences & Healthcare, Financial Services, Insurance, Retail & Distribution, Energy & Resources, Media & Business Information Services.
Also In EPAM a skill head also referred as Practice Head is a senior leadership role responsible for managing and growing a specific practice area or domain.

Each practice head is required to conduct advanced research by reviewing credible sources such as articles, white papers, reports, and publications from leading industry bodies, organizations, and thought leaders. The goal is to identify and analyze predictions for the next 3-5 years from ${year}.
Specifically, practice heads should address the following:
 
1. Emerging Trends and Technologies: What key trends, technologies, or industry shifts are forecasted to shape the practice in the next 3-5 years from ${year}?
2. Growing Demands and Market Needs: What are the evolving demands or priorities of customers, businesses, or industries that will influence the practice area?
3. Data-Driven Insights: What evidence, data, or reasoning do the sources provide to support these predictions?
4. Alignment with Industry Standards: How do these predictions align with insights from standard-setting bodies, regulatory organizations, or influential thought leaders in the domain?
5. Strategic Implications: What strategic opportunities or challenges might arise for the practice based on these forecasts?
 
The research should focus on identifying actionable insights that can help the practice stay ahead of the curve, adapt to technological advancements, and meet future demands effectively.

Sample response structure :

1.Category 1
  combined answer for all the questions within a category
2.Category 2
  combined answer for all the questions within a category
 
## Role ##
As a Practice Head of ${formData.practiceName} practice:

## Task ##
Identify the future skills essential for the ${formData.practiceName} practice, addressing the following areas with a futuristic and strategic focus on EPAM Product Development Services. 
Base your each responses on credible research from sources like industry reports, research papers, and market analysis.
Also Share references of industry standards, sources for each answer in italic font in a separate line.

`;
 
      formData.categories.forEach((category, index) => {
        if (Array.isArray(category.questions) && category.questions.length > 0) {
          comprehensivePrompt += ` \n${index + 1}. ${category.categoryName}\n `;
          category.questions.forEach((question, qIndex) => {
            comprehensivePrompt += ` - ${question}\n `;
          });
        } else {
          if (category.categoryName !== "Future Skills (3–5 years)") {
            comprehensivePrompt += `${index}. ${category.categoryName}\n`;
          }
        }
      });
      comprehensivePrompt += `

## Instruction ##
- Maintain the original category names with out any changes.
- Ensure advanced-level insights with a focus on helping EPAM India become a market leader.
- Here all the categories are of a numbered list and all the bullet points are questions within that category, while answering give a combined answer to all the questions within 100-150 words.
- Use plain text only - NO Markdown symbols (#, *, -)

## Settings ##
temperature: 0.2
top_p: 0.3
max_tokens: 4000`;
 
      const apiKey = process.env.REACT_APP_OPENAI_API_KEY;
      const endpoint =
        "https://ai-proxy.lab.epam.com/openai/deployments/gemini-2.5-pro-exp-03-25/chat/completions?api-version=2024-02-01";
 
      const requestBody = {

        messages: [{ role: "user", content: comprehensivePrompt }],

        temperature: 0.2,

      };

      const requestHeaders = {
        "Content-Type": "application/json",
        "api-key": apiKey,
      };
 
      const response = await axios.post(endpoint, requestBody, {
        headers: requestHeaders,
      });
      return response.data.choices[0].message.content;
 
    } catch (error) {

      if (error.response) {
        return `API Error: ${error.response.status} - ${error.response.data.error?.message || "Unknown API error"}`;

      } else if (error.request) {
        return "Network Error: No response received from the server.";
      } else {
        return `Unknown Error: ${error.message}`;
      }
    }
  }
};
 
export const parseDependentResponses = (aiResponse, dependentCategories) => {

  const result = {};
  let currentCategory = null;
  let currentContent = [];
  const lines = aiResponse.split("\n");
 
  for (const line of lines) {

    const categoryMatch = dependentCategories.find(
      (cat) => line.trim() === cat || line.trim().startsWith(cat + ":")
    );
 
    if (categoryMatch) {

      if (currentCategory && currentContent.length > 0) {
        result[currentCategory] = currentContent.join("\n");
      }

      currentCategory = categoryMatch;
      currentContent = [];

    } else if (currentCategory && line.trim()) {
      currentContent.push(line.trim());
    }
  }
 
  if (currentCategory && currentContent.length > 0) {
    result[currentCategory] = currentContent.join("\n");
  }
 
  dependentCategories.forEach((cat) => {
    if (!result[cat]) {
      result[cat] = "";
    }
  });

  return result;
};
 