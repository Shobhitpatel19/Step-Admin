/* eslint-disable testing-library/no-wait-for-side-effects */
/* eslint-disable jest/no-conditional-expect */
/* eslint-disable testing-library/no-wait-for-multiple-assertions */
import {
    render,
    screen,
    fireEvent,
    waitFor,
} from "@testing-library/react";
import axios from "axios";
import { Provider } from "react-redux";
import { configureStore } from "@reduxjs/toolkit";
import { BrowserRouter } from "react-router-dom";
import rootReducer from "../../src/redux/combinedreducer";
import FutureSkills from "../components/pages/Future_skills/FutureSkills";
import axiosInstance from "../../src/components/common/axios";
import { UuiContext, useUuiServices, StubAdaptedRouter } from "@epam/uui-core";
import { notify } from "../redux/actions";
import * as reduxHooks from 'react-redux';

function UuiContextDefaultWrapper({ children }) {
    const testUuiCtx = {};
    const router = new StubAdaptedRouter();
    const { services } = useUuiServices({ router });

    const mockInitialState = {
        notifications: {},
    };

    const store = configureStore({
        reducer: rootReducer,
        preloadedState: mockInitialState,
    });

    Object.assign(testUuiCtx, services);

    return (
        <Provider store={store}>
            <BrowserRouter>
                <UuiContext.Provider value={services}>
                    {children}
                </UuiContext.Provider>
            </BrowserRouter>
        </Provider>
    );
}

jest.mock("axios", () => ({
    create: jest.fn(() => ({
        interceptors: {
            request: { use: jest.fn() },
            response: { use: jest.fn() },
        },
        get: jest.fn(),
        post: jest.fn(),
        put: jest.fn(),
        delete: jest.fn(),
        patch: jest.fn(),
    })),
    post: jest.fn().mockResolvedValue({
        data: {
            choices: [{ message: { content: "AI generated response" } }]
        }
    })
}));

jest.mock('../redux/actions', () => ({
    notify: jest.fn()
}));

jest.mock('react-redux', () => ({
    ...jest.requireActual('react-redux'),
    useDispatch: jest.fn(),
}));

const mockedNavigate = jest.fn();
jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockedNavigate,
}));


process.env.REACT_APP_OPENAI_API_KEY = 'mock-api-key';

async function renderToJsDom(reactElement) {
    const view = render(reactElement, { wrapper: UuiContextDefaultWrapper });
    return view;
}

describe("FutureSkills Component Tests", () => {
    const mockDispatch = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
        jest.spyOn(reduxHooks, 'useDispatch').mockReturnValue(mockDispatch);
    });

    const mockData = {
        practiceHeadName: "John Doe",
        practiceName: "Cloud Development",
        submissionStatus: "D",
        lastUpdated: "2023-07-10T12:30:45Z",
        categories: [
            {
                categoryName: "Future Skills (3–5 years)",
                answer: "React, Node.js, AWS"
            },
            {
                categoryName: "Emerging Skills",
                answer: "AI/ML, Blockchain",
                questions: ["What emerging skills do you see becoming important?"]
            },
            {
                categoryName: "Current Skills",
                answer: "JavaScript, TypeScript, Python",
                questions: []
            },
            {
                categoryName: "Expected Time to Readiness",
                answer: "6-12 months",
                questions: ["How long until these skills can be implemented?"]
            },
            {
                categoryName: "Opportunities & Business Problems Solved",
                answer: "Improved client solutions",
                questions: ["What business problems can be solved?"]
            },
            {
                categoryName: "Estimated Budget Requirement Per Person",
                answer: "$5000",
                questions: ["What budget is required?"]
            }
        ]
    };

    it("displays error modal when API call fails", async () => {
        axiosInstance.get.mockRejectedValue(new Error("API Error"));

        await renderToJsDom(<FutureSkills />);

        await waitFor(() => {
            expect(screen.getByText(/Inital merit list is not uploaded yet\./i)).toBeInTheDocument();
            expect(screen.getByText(/Go to Home/i)).toBeInTheDocument();
        });
    });

    it("allows adding and removing future skills", async () => {
        axiosInstance.get.mockResolvedValue({ status: 200, data: mockData });

        await renderToJsDom(<FutureSkills />);

        await waitFor(() => {

            expect(screen.getByText("React")).toBeInTheDocument();
            expect(screen.getByText("Node.js")).toBeInTheDocument();
            expect(screen.getByText("AWS")).toBeInTheDocument();
        });

 
        const skillInput = screen.getByPlaceholderText("Enter future skill");
        fireEvent.change(skillInput, { target: { value: "GraphQL" } });

        const addButton = screen.getByText("Add Skill");
        fireEvent.click(addButton);

    
        await waitFor(() => {
            expect(screen.getByText("GraphQL")).toBeInTheDocument();
        });

  
        const removableTags = screen.getAllByRole("button");
        const removeNodeButton = removableTags.find(button =>
            button.textContent.includes("Node.js") || button.getAttribute("aria-label")?.includes("Node.js")
        );

        if (removeNodeButton) {
            fireEvent.click(removeNodeButton);
            await waitFor(() => {
                expect(screen.queryByText("Node.js")).not.toBeInTheDocument();
            });
        }
    });

    

    it("closes confirmation modal when Cancel button is clicked", async () => {
        const completeData = {
            ...mockData,
            submissionStatus: "D",
            categories: mockData.categories.map(cat => ({ ...cat, answer: cat.answer || "Sample answer" }))
        };

        axiosInstance.get.mockResolvedValue({ status: 200, data: completeData });

        await renderToJsDom(<FutureSkills />);

        const submitButton = screen.getByText("Submit");
        fireEvent.click(submitButton);

        await waitFor(() => {
            const cancelButton = screen.getByText("Cancel");
            fireEvent.click(cancelButton);
        });

        await waitFor(() => {
            expect(screen.queryByText("Confirm Submission")).not.toBeInTheDocument();
        });
    });
      it("shows error notification when draft save fails", async () => {
        axiosInstance.get.mockResolvedValue({ status: 200, data: mockData });
        axiosInstance.post.mockRejectedValue(new Error("API Error"));
        
        await renderToJsDom(<FutureSkills />);
        
        await waitFor(() => {
          expect(screen.queryByText(/Loading.../i)).not.toBeInTheDocument();
        });
        
        const emergingSkillsTitle = await screen.findAllByText("Emerging Skills");
        fireEvent.click(emergingSkillsTitle[0]); 
        
        await waitFor(() => {
          const textarea = screen.getByDisplayValue("AI/ML, Blockchain");
          fireEvent.change(textarea, { target: { value: "New emerging skills" } });
        });
       
        const draftButton = screen.getByText("Save as Draft");
        fireEvent.click(draftButton);
        
        await waitFor(() => {
          expect(notify).toHaveBeenCalledWith(
            "Error saving data. Please try again later.", false
          );
        });
      });
  

      it("prevents adding duplicate future skills", async () => {
        axiosInstance.get.mockResolvedValue({ status: 200, data: mockData });
        
        await renderToJsDom(<FutureSkills />);
        
        await waitFor(() => {
          expect(screen.queryByText(/Loading.../i)).not.toBeInTheDocument();
        });
     
        const skillInput = screen.getByPlaceholderText("Enter future skill");
        fireEvent.change(skillInput, { target: { value: "React" } });
        
        const addButton = screen.getByText("Add Skill");
        fireEvent.click(addButton);
     
        await waitFor(() => {
          expect(notify).toHaveBeenCalledWith("Skill already added.", false);
        });
      });
      
      

  it("updates all dependent categories when one category is regenerated", async () => {
 
    axios.post.mockReset();

    axios.post.mockImplementation((url, data) => {

      if (data.messages && data.messages[0]?.content.includes("Emerging Skills")) {
        return Promise.resolve({
          data: {
            choices: [
              {
                message: {
                  content: "AI/ML, Quantum Computing, Edge Computing"
                }
              }
            ]
          }
        });
      } 

      else if (data.messages && data.messages[0]?.content.includes("dependent")) {
        return Promise.resolve({
          data: {
            choices: [
              {
                message: {
                  content: `Expected Time to Readiness
  12-18 months
  
  Opportunities & Business Problems Solved
  Enhanced data processing, improved security, scalable infrastructure
  
  Estimated Budget Requirement Per Person
  $8000`
                }
              }
            ]
          }
        });
      }
 
      else {
        return Promise.resolve({
          data: {
            choices: [{ message: { content: "Default response" } }]
          }
        });
      }
    });
    
    axiosInstance.get.mockResolvedValue({ status: 200, data: mockData });
    
    await renderToJsDom(<FutureSkills />);
    
    await waitFor(() => {
      expect(screen.queryByText(/Loading.../i)).not.toBeInTheDocument();
    });
    
 
    const accordionTitles = await screen.findAllByText("Emerging Skills");
    fireEvent.click(accordionTitles[0]);
    
    
    await waitFor(() => {
      const regenerateButtons = screen.getAllByText("Re-generate AI");
      fireEvent.click(regenerateButtons[0]);
    });

    await waitFor(() => {
      const continueButton = screen.getByText("Continue");
      fireEvent.click(continueButton);
    });
 
    await waitFor(() => {
     
      expect(axios.post).toHaveBeenCalledTimes(3);
      
   
      expect(notify).toHaveBeenCalledWith(
        "Successfully updated dependent categories based on new information",
        true
      );
    });
    
  
    const timeToReadinessTitle = await screen.findAllByText("Expected Time to Readiness");
    fireEvent.click(timeToReadinessTitle[0]);
    
  
    await waitFor(() => {
      const textareaElements = screen.getAllByRole("textbox");
      const updatedTextarea = textareaElements.find(
        textarea => textarea.value === "12-18 months"
      );
  
    });
  });
});



