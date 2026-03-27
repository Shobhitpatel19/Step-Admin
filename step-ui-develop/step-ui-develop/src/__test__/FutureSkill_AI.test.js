import { generateAIResponse,parseDependentResponses} from '../../src/components/pages/Future_skills/FutureSkill_AI';
import axios from 'axios';

jest.mock("axios", () => ({
  post: jest.fn(),
  get: jest.fn(),
  put: jest.fn(),
  delete: jest.fn(),
}));
 
jest.mock("react-router-dom", () => ({
  ...jest.requireActual("react-router-dom"),
  useNavigate: () => jest.fn(),
}));
describe('parseDependentResponses', () => {
  const dependentCategories = ['Summary', 'Analysis', 'Recommendations'];
 
  test('should parse simple response with all categories', () => {
    const aiResponse = `Summary\nThis is the summary\n\nAnalysis\nThis is the analysis\n\nRecommendations\nThese are recommendations`;
    const expected = {
      Summary: 'This is the summary',
      Analysis: 'This is the analysis',
      Recommendations: 'These are recommendations'
    };
    expect(parseDependentResponses(aiResponse, dependentCategories)).toEqual(expected);
  });
 
  test('should handle empty response', () => {
    const aiResponse = '';
    const expected = {
      Summary: '',
      Analysis: '',
      Recommendations: ''
    };
    expect(parseDependentResponses(aiResponse, dependentCategories)).toEqual(expected);
  });
 
  test('should handle response with missing categories', () => {
    const aiResponse = `Summary\nThis is the summary\n\nRecommendations\nThese are recommendations`;
    const expected = {
      Summary: 'This is the summary',
      Analysis: '',
      Recommendations: 'These are recommendations'
    };
    expect(parseDependentResponses(aiResponse, dependentCategories)).toEqual(expected);
  });
 
  test('should handle response with extra content before first category', () => {
    const aiResponse = `Some preamble\nSummary\nThis is the summary`;
    const expected = {
      Summary: 'This is the summary',
      Analysis: '',
      Recommendations: ''
    };
    expect(parseDependentResponses(aiResponse, dependentCategories)).toEqual(expected);
  });
 
 
  test('should preserve line breaks within category content', () => {
    const aiResponse = `Summary\nLine 1\nLine 2\nLine 3`;
    const expected = {
      Summary: 'Line 1\nLine 2\nLine 3',
      Analysis: '',
      Recommendations: ''
    };
    expect(parseDependentResponses(aiResponse, dependentCategories)).toEqual(expected);
  });
 
  test('should handle empty lines between categories', () => {
    const aiResponse = `Summary\nContent\n\n\nAnalysis\nMore content`;
    const expected = {
      Summary: 'Content',
      Analysis: 'More content',
      Recommendations: ''
    };
    expect(parseDependentResponses(aiResponse, dependentCategories)).toEqual(expected);
  });
 
  test('should handle case where category appears multiple times', () => {
    const aiResponse = `Summary\nFirst part\nSummary\nSecond part`;
    const expected = {
      Summary: 'Second part',
      Analysis: '',
      Recommendations: ''
    };
    expect(parseDependentResponses(aiResponse, dependentCategories)).toEqual(expected);
  });
 
  test('should handle categories with different order than declared', () => {
    const aiResponse = `Recommendations\nFirst\nAnalysis\nSecond\nSummary\nThird`;
    const expected = {
      Summary: 'Third',
      Analysis: 'Second',
      Recommendations: 'First'
    };
    expect(parseDependentResponses(aiResponse, dependentCategories)).toEqual(expected);
  });
 
  test('should handle empty category content', () => {
    const aiResponse = `Summary\nAnalysis\n\nRecommendations\nContent`;
    const expected = {
      Summary: '',
      Analysis: '',
      Recommendations: 'Content'
    };
    expect(parseDependentResponses(aiResponse, dependentCategories)).toEqual(expected);
  });
});
describe('generateAIResponse', () => {
  const originalEnv = process.env;
  
  beforeEach(() => {
    process.env.REACT_APP_OPENAI_API_KEY = 'test-api-key';
    jest.clearAllMocks();
  });
  
  afterEach(() => {
    process.env = originalEnv;
  });

  test('should generate AI response with valid practice name and categories', async () => {
    const mockDate = new Date(2023, 0, 1); 
    const realDate = global.Date;
    global.Date = jest.fn(() => mockDate);
    global.Date.now = realDate.now;
    const formData = {
      practiceName: 'JavaScript Development',
      categories: [
        {
          categoryName: 'Critical Behavioral Competencies',
          questions: ['Question 1', 'Question 2']
        },
        {
          categoryName: 'Industry & Market Trends',
          questions: ['Question 3']
        }
      ]
    };
    const mockResponse = {
      data: {
        choices: [
          {
            message: {
              content: 'AI generated response'
            }
          }
        ]
      }
    };
    
    axios.post.mockResolvedValue(mockResponse);
    const result = await generateAIResponse(formData);
    expect(result).toBe('AI generated response');
    expect(axios.post).toHaveBeenCalledTimes(1);
    expect(axios.post.mock.calls[0][0]).toBe(
      'https://ai-proxy.lab.epam.com/openai/deployments/gemini-2.5-pro-exp-03-25/chat/completions?api-version=2024-02-01'
    );
    const requestBody = axios.post.mock.calls[0][1];
    expect(requestBody.messages[0].content).toContain('JavaScript Development');
    expect(requestBody.messages[0].content).toContain('2023'); 
    expect(requestBody.messages[0].content).toContain('Critical Behavioral Competencies');
    expect(requestBody.messages[0].content).toContain('Question 1');
    expect(requestBody.messages[0].content).toContain('Industry & Market Trends');
    expect(requestBody.temperature).toBe(0.2);
    const headers = axios.post.mock.calls[0][2].headers;
    expect(headers['Content-Type']).toBe('application/json');
    expect(headers['api-key']).toBe('test-api-key');
    global.Date = realDate;
  });
  test('should handle API error with response', async () => {
    const formData = {
      practiceName: 'Cloud Computing',
      categories: []
    };
    axios.post.mockRejectedValue({
      response: {
        status: 400,
        data: {
          error: {
            message: 'Invalid request parameters'
          }
        }
      }
    });
    
    const result = await generateAIResponse(formData);
    
    expect(result).toBe('API Error: 400 - Invalid request parameters');
  });

  test('should handle network error', async () => {
    const formData = {
      practiceName: 'Data Science',
      categories: []
    };
    axios.post.mockRejectedValue({
      request: {},
      message: 'Network Error'
    });
    
    const result = await generateAIResponse(formData);
    
    expect(result).toBe('Network Error: No response received from the server.');
  });

  test('should handle unknown error', async () => {
    const formData = {
      practiceName: 'DevOps',
      categories: []
    };
    axios.post.mockRejectedValue({
      message: 'Something went wrong'
    });
    
    const result = await generateAIResponse(formData);
    
    expect(result).toBe('Unknown Error: Something went wrong');
  });

  test('should handle empty categories array', async () => {
    const formData = {
      practiceName: 'Blockchain',
      categories: []
    };
    axios.post.mockResolvedValue({
      data: {
        choices: [{ message: { content: 'Response' } }]
      }
    });
    
    const result = await generateAIResponse(formData);
    
    expect(result).toBe('Response');
    expect(axios.post).toHaveBeenCalled();
    expect(axios.post.mock.calls[0][1].messages[0].content).toContain('Blockchain');
  });
});