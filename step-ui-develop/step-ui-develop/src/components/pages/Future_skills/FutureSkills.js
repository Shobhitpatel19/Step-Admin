import React, { useEffect, useState } from "react";
import "../Future_skills/FutureSkills.css";
import { getTokenFromCookies, decodeToken } from "../../utils/auth";
import {
     Button,
     ModalBlocker,
     ModalWindow,
     ModalFooter,
     ModalHeader,
     ScrollBars,
     FlexRow,
     FlexCell,
     Text,
     LabeledInput,
     TextInput,
     Accordion,
     TextArea,
     Tag,
     Panel,
} from "@epam/uui";
import axiosInstance from "../../common/axios";
import Alert from "../../common/Alert";
import { useDispatch } from "react-redux";
import { notify } from "../../../redux/actions";
import { NavbarForP } from "../../pages/landing_page/navigation_p";

import { generateAIResponse, parseDependentResponses } from "./FutureSkill_AI";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import FormFooter from "./FormFooter";

export const ClearableTextInput = ({ value, onValueChange, isDisabled, ...props }) => {

     return (
          <div style={{ position: "relative" }}>
               <TextInput
                    value={value}
                    onValueChange={onValueChange}
                    isDisabled={isDisabled}
                    {...props}
               />
               {value && !isDisabled && (
                    <button
                         style={{
                              position: "absolute",
                              right: "10px",
                              top: "50%",
                              transform: "translateY(-50%)",
                              background: "transparent",
                              border: "none",
                              cursor: "pointer",
                              fontSize: "16px",
                         }}
                         onClick={() => onValueChange("")}
                    > ✖
                    </button>
               )}
          </div>
     );
};

function FutureSkills() {
     const initialState = {
          practiceHeadName: "",
          practiceName: "",
          submissionStatus: "NA",
          categories: [],
          lastupdated: null,
     };
     const [firstName, setFirstName] = useState("Guest");
     const [formData, setFormData] = useState(initialState);
     const [answers, setAnswers] = useState({});
     const [disableFields, setDisableFields] = useState(false);
     const [showModal, setShowModal] = useState(false);
     const [SubmissionStatus, setSubmissionStatus] = useState("");
     const [isSaved, setIsSaved] = useState(false);
     const [errors, setErrors] = useState({});
     const [futureSkillsList, setFutureSkillsList] = useState([]);
     const [currentFutureSkill, setCurrentFutureSkill] = useState("");
     const [isEditing, setIsEditing] = useState({});
     const [regenerateCategoryName, setRegenerateCategoryName] = useState("");
     const [expandedAccordions, setExpandedAccordions] = useState({});
     const [loadingAI, setLoadingAI] = useState({});
     const [AIMap, setAIMap] = useState({});
     const [lastupdated, setLastUpdated] = useState(null);
     const [lastSubmittedData, setLastSubmittedData] = useState(null);
     const [userInput, setUserInput] = useState("");
     const [isOpen, setIsOpen] = useState(false);
     const [disableSubmit, setDisableSubmit] = useState(true);
     const [disableDraft, setDisableDraft] = useState(true);
     const [error, setError] = useState(false);
     const navigate = useNavigate();
     const dispatch = useDispatch();


     const generateSingleCategoryResponse = async (categoryName) => {
          setLoadingAI((prev) => ({ ...prev, [categoryName]: true }));
          try {
               const category = formData.categories.find(
                    (cat) => cat.categoryName === categoryName
               );

               if (!category || !category.questions || category.questions.length === 0) {
                    dispatch(notify("No questions found for this category.", false));
                    return;
               }

               let specificPrompt = `## Context ##
      We are building a system to help top employees at EPAM India develop future-oriented skills for their practice. Each practice head identifies each future-critical skills through advanced research (articles, white papers, and reports) that will enhance productivity and business impact in the coming years.

      ## Role ##
      As a Practice Head of ${formData.practiceName} practice:

      ## Task ##
      Identify the future skills essential for the ${formData.practiceName} practice, focusing specifically on the following category: ${categoryName}

      ## Questions to address: ##
      `;
               category.questions.forEach((question) => {
                    specificPrompt += ` - ${question}\n `;
               });

               specificPrompt += `
      ## Instruction ##
      - Provide a clear, concise response in 20-30 words overall.
      - Ensure advanced-level insights with a focus on helping EPAM India become a market leader.
      - Use plain text only - NO Markdown symbols (#, *, -)
      -do not give questions
      -response for 1st question
      -response for 2nd question
      -response for 3rd question

      ## Settings ##
      temperature: 0.2
      top_p: 0.3
      max_tokens: 800`;

               const apiKey = process.env.REACT_APP_OPENAI_API_KEY;
               const endpoint =
                    "https://ai-proxy.lab.epam.com/openai/deployments/gpt-4/chat/completions?api-version=2024-02-01";

               const requestBody = {
                    messages: [{ role: "user", content: specificPrompt }],
                    temperature: 0.2,
               };

               const requestHeaders = {
                    "Content-Type": "application/json",
                    "api-key": apiKey,
               };

               const response = await axios.post(endpoint, requestBody, {
                    headers: requestHeaders,
               });

               const aiResponse = response.data.choices[0].message.content;

               setAIMap((prevMap) => {
                    const newMap = new Map(prevMap);
                    newMap.set(categoryName, aiResponse);
                    return newMap;
               });

               dispatch(
                    notify(`Successfully regenerated content for ${categoryName}`, true)
               );
               await regenerateDependentCategories(AIMap, formData);
               return aiResponse;

          } catch (error) {
               dispatch(
                    notify(
                         `Error generating response for ${categoryName}. Please try again.`,
                         false
                    )
               );
               return null;
          } finally {
               setLoadingAI((prev) => ({ ...prev, [categoryName]: false }));
          }
     };

     const regenerateDependentCategories = async (updatedAIMap, formData) => {
          const dependentCategories = [
               "Expected Time to Readiness",
               "Opportunities & Business Problems Solved",
               "Estimated Budget Requirement Per Person",
          ];
          setLoadingAI((prev) => {
               const newLoadingState = { ...prev };
               dependentCategories.forEach((cat) => {
                    newLoadingState[cat] = true;
               });
               return newLoadingState;
          });

          try {
               let dependentPrompt = `## Context ##
  We are building a system to help top employees at EPAM India develop future-oriented skills for their practice. Each practice head identifies future-critical skills through advanced research that will enhance productivity and business impact.
 
  ## Role ##
  As a Practice Head of ${formData.practiceName} practice:
 
  ## Current Skills Assessment ##
  Based on the current assessment for ${formData.practiceName}, we have the following information:
  these are the top talents of epam so while answer everything keep that in mind they are also quick learners
  Now i will be sharing the information of the response based on that give me response for
  "Expected Time to Readiness",
  "Opportunities & Business Problems Solved",
  "Estimated Budget Requirement Per Person"
  this three categories do not change the name of the categories and the resposne structure should look like
  Expected Time to Readiness
  -response
  `;
               updatedAIMap.forEach((response, categoryName) => {
                    if (!dependentCategories.includes(categoryName)) {
                         dependentPrompt += `\n${categoryName}:\n${response}\n`;
                    }
               });

               dependentPrompt += `\n## Task ##
  Based on the above information about ${formData.practiceName} practice, provide specific responses for the following categories:
  `;

               dependentCategories.forEach((categoryName) => {
                    const category = formData.categories.find(
                         (cat) => cat.categoryName === categoryName
                    );
                    if (category && category.questions && category.questions.length > 0) {
                         dependentPrompt += `\n${categoryName}:\n`;
                         category.questions.forEach((question) => {
                              dependentPrompt += `- ${question}\n`;
                         });
                    }
               });

               dependentPrompt += `
  ## Instructions ##
  - Provide clear, concise responses in 20-30 words for each question.
  - Ensure your responses are directly based on and consistent with the skills and information provided above.
  - Format your response as plain text with no Markdown symbols.
  - we have three categories which are
  ## Settings ##
  temperature: 0.2
  top_p: 0.3
  max_tokens: 1000`;

               const apiKey = process.env.REACT_APP_OPENAI_API_KEY;
               const endpoint =
                    "https://ai-proxy.lab.epam.com/openai/deployments/gpt-4/chat/completions?api-version=2024-02-01";

               const requestBody = {
                    messages: [{ role: "user", content: dependentPrompt }],
                    temperature: 0.2,
               };

               const requestHeaders = {
                    "Content-Type": "application/json",
                    "api-key": apiKey,
               };

               const response = await axios.post(endpoint, requestBody, {
                    headers: requestHeaders,
               });

               const aiResponse = response.data.choices[0].message.content;
               const categoryResponses = parseDependentResponses(aiResponse, dependentCategories);

               setAIMap((prevMap) => {
                    const newMap = new Map(prevMap);
                    Object.entries(categoryResponses).forEach(([category, response]) => {
                         newMap.set(category, response);
                    });
                    return newMap;
               });
               dispatch(notify("Successfully updated dependent categories based on new information", true));
               return categoryResponses;
          } catch (error) {
               dispatch(notify("Error updating dependent categories. Please try again.", false));
               return null;
          } finally {
               setLoadingAI((prev) => {
                    const newLoadingState = { ...prev };
                    dependentCategories.forEach((cat) => {
                         newLoadingState[cat] = false;
                    });
                    return newLoadingState;
               });
          }
     };

     var count = 0;

     useEffect(() => {
          fetchFutureSkills();
          if (formData) {
               if (count < 1) {
                    let response = generateAIResponse(formData);
                    response.then((data) => {

                         const map = new Map();
                         const sections = data
                              .trim()
                              .split(/\n\d+\./)
                              .slice(0);

                         sections.forEach((section) => {
                              const [title, ...content] = section
                                   .replace(/^\d+\.\s*/, "")
                                   .trim()
                                   .split("\n");
                              map.set(title.trim(), content.join("\n").trim());
                         });
                         setAIMap(map);
                    });
               }
               count = count + 1;
          }
     }, []);

     const fetchFutureSkills = async () => {
          try {
               const response = await axiosInstance.get("/step/future-skills");
               const data = response.data;
               setSubmissionStatus(data.submissionStatus);

               const initialAnswers = {};
               const initialFutureSkills = [];

               data.categories.forEach((category) => {
                    initialAnswers[category.categoryName] = category.answer !== (null || "") ? category.answer : "";
                    if (category.categoryName === "Future Skills (3–5 years)" &&category.answer) {
                         initialFutureSkills.push(
                              ...category.answer
                                   .split(",")
                                   .map((skill) => skill.trim())
                                   .filter((skill) => skill)
                         );
                    }
                    setIsEditing((prev) => ({ ...prev, [category.categoryName]: false }));
               });
               setAnswers(initialAnswers);
               setFutureSkillsList(initialFutureSkills);
               setLastUpdated(data.lastUpdated);

               setFormData({
                    practiceHeadName: data.practiceHeadName || "",
                    practiceName: data.practiceName || "",
                    submissionStatus: data.submissionStatus || "NA",
                    categories: data.categories || [],
                    lastupdated: data.lastUpdated || null,
               });

               const result = data.categories.reduce((acc, item) => {
                    acc[item.categoryName] = item.answer;
                    return acc;
               }, {});

               setLastSubmittedData(result);

          } catch (error) {
               setError(true);
          }
     };

     const handleInputChange = async (categoryName, value) => {

          setAnswers((prev) => ({ ...prev, [categoryName]: value }));
          setErrors((prevErrors) => ({ ...prevErrors, [categoryName]: undefined }));

     };

     useEffect(() => {

          setDisableDraft(true);

          if (JSON.stringify(answers) !== JSON.stringify(lastSubmittedData)) {
               setDisableDraft(false);
               if (Object.values(answers).every((val) => val && val.trim() !== '')) {
                    setDisableSubmit(false)
               }
               else {
                    setDisableSubmit(true)
               }
          }
          else {
               setDisableDraft(true);
               setDisableSubmit(SubmissionStatus === "D" && Object.values(lastSubmittedData).every((val) => val && val.trim() !== '') ? false : true);
          }
          if (Object.values(answers).every((val) => val === "" || val === null)) {
               setDisableDraft(true)
          }

     }, [answers, lastSubmittedData]);

     const handleSubmission = async (status) => {
          try {
               const updatedAnswers = {
                    ...answers,
                    "Future Skills (3–5 years)": futureSkillsList.join(", "),
               };
               const payload = {
                    submissionStatus: status,
                    lastUpdated: new Date().toISOString(),
                    futureSkills: formData.categories.map((cat) => ({
                         categoryName: cat.categoryName,
                         answer: updatedAnswers[cat.categoryName] || "",
                    })),
               };
               await axiosInstance.post("/step/future-skills", payload);
               setLastUpdated(payload.lastUpdated);
               dispatch(notify(status === "D" ? "Data has been drafted successfully" : "Submitted Successfully", true));
               setLastSubmittedData(updatedAnswers);
               setSubmissionStatus(status);

               if (status === "S") {
                    setShowModal(false)
               }
          } catch (error) {
               dispatch(notify("Error saving draft. Please try again later.", false));
          }
     };

     const handleSave = () => {
          setShowModal(true);
     };

     const CancelSave = () => {
          setShowModal(false);
     };

     useEffect(() => {
          const token = getTokenFromCookies();
          if (token) {
               const { firstName } = decodeToken(token);
               setFirstName(firstName);
          }
     }, []);

     const handleAddFutureSkill = () => {
          if (currentFutureSkill.trim() && !futureSkillsList.includes(currentFutureSkill.trim())) {

               futureSkillsList.push(currentFutureSkill)
               let str = futureSkillsList.join(', ');
               setAnswers((prev) => {
                    return {
                         ...prev,
                         "Future Skills (3–5 years)": str
                    }
               });
               setCurrentFutureSkill("");
               setErrors((prevErrors) => ({
                    ...prevErrors,
                    ["Future Skills (3–5 years)"]: undefined,
               }));
          } else if (futureSkillsList.includes(currentFutureSkill.trim())) {
               dispatch(notify("Skill already added.", false));
          }
     };

     const handleRemoveFutureSkill = (skillToRemove) => {

          let str = futureSkillsList.filter((skill) => skill !== skillToRemove).join(", ");
          setAnswers((prev) => {
               return {...prev,"Future Skills (3–5 years)": str}
          });

          setFutureSkillsList(
               futureSkillsList.filter((skill) => skill !== skillToRemove)
          );
     };

     if (!formData) return <p>Loading...</p>;
     return (
          <div>
               <NavbarForP hideContent={true} />
               <FlexCell
                    style={{display: "flex",flex: "1", justifyContent: "center", flexDirection: "column",
                    }}
                    rawProps={{ "data-testid": "second-component" }}
               >
                    
               </FlexCell>
               <div className="future-skill-container">
                    <form className="future-skill-form">
                         {error === true &&
                              <div style={{ zIndex: "100" }} >
                                   <ModalBlocker >
                                        <ModalWindow className="large-modal">
                                             <ModalHeader
                                                  title="Cannot Upload Future Skills"
                                                  rawProps={{
                                                       style: { paddingBottom: "5px" }
                                                  }}
                                                  borderBottom={true}
                                             />
                                             <FlexRow padding="24" >
                                                  <Text fontSize="18">Inital merit list is not uploaded yet.</Text>
                                             </FlexRow>
                                             <ModalFooter className="modal-footer"
                                                  rawProps={{
                                                       style: { justifyContent: "end" }
                                                  }}
                                             >
                                                  <Button
                                                       color="secondary"
                                                       fill="outline"
                                                       caption="Go to Home"
                                                       onClick={() => { navigate("/") }}
                                                  />
                                             </ModalFooter>
                                        </ModalWindow>
                                   </ModalBlocker>
                              </div>
                         }
                         {error === false &&
                              <div>
                                   <div className="form-columns">
                                        <FlexCell grow={1}>
                                             <LabeledInput label="Practice Head Name">
                                                  <ClearableTextInput
                                                       value={formData.practiceHeadName}
                                                       isDisabled
                                                  />
                                             </LabeledInput>
                                        </FlexCell>
                                        <FlexCell grow={1}>
                                             <LabeledInput label="Practice Name">
                                                  <ClearableTextInput
                                                       value={formData.practiceName}
                                                       isDisabled
                                                  />
                                             </LabeledInput>
                                        </FlexCell>
                                   </div>

                                   <FlexCell style={{ display: "flex", flexDirection: "column", gap: "16px" }}>

                                        {formData.categories.map((category, index) => {
                                             if (category.questions && category.questions.length > 0) {
                                                  return (
                                                       <Accordion
                                                            title={
                                                                 <>
                                                                      {category.categoryName}
                                                                      <span style={{ color: "red", marginLeft: "5px" }}>*</span>
                                                                 </>
                                                            }
                                                            value={expandedAccordions[category.categoryName] || false}
                                                            key={index}
                                                            onValueChange={async () => {
                                                                 const newValue = !expandedAccordions[category.categoryName];
                                                                 setExpandedAccordions((prev) => ({
                                                                      ...prev,
                                                                      [category.categoryName]: newValue,
                                                                 }));
                                                            }}
                                                       >
                                                            <div
                                                                 style={{
                                                                      display: "flex",
                                                                      alignItems: "center",
                                                                      gap: "20px",
                                                                      width: "100%",
                                                                 }}
                                                            >
                                                                 <div style={{ flex: 1}}>
                                                                      {category.questions.map((question, qIndex) => (
                                                                           <p key={qIndex} style={{ fontSize: "14px", marginBottom: "8px",fontStyle:"italic"}}>
                                                                                {question}
                                                                           </p>
                                                                      ))}
                                                                 </div>
                                                            </div>
                                                            <div>
                                                                 <FlexRow spacing="12">
                                                                      <FlexCell grow={1} style={{ position: "relative" }}>
                                                                           <TextArea
                                                                                value={
                                                                                     AIMap.size > 0
                                                                                          ? AIMap.get(category.categoryName)
                                                                                          : "AI is generating a response for you..."
                                                                                }
                                                                                onValueChange={setUserInput}
                                                                                placeholder="Answer generated by AI will appear here"
                                                                                isDisabled={true}
                                                                                rows={6}
                                                                           />
                                                                      </FlexCell>

                                                                      <FlexCell grow={1}>
                                                                           <TextArea
                                                                                value={answers[category.categoryName] || ""}
                                                                                rows={12}
                                                                                autoFocus={true}
                                                                                onValueChange={(val) => handleInputChange(category.categoryName, val)}
                                                                                placeholder=" Write your response here"
                                                                           />
                                                                      </FlexCell>

                                                                      {errors[category.categoryName] && (
                                                                           <p style={{ color: "red" }}>{errors[category.categoryName]}</p>
                                                                      )}


                                                                 </FlexRow>

                                                                 {errors[category.categoryName] && (
                                                                      <p style={{ color: "red" }}>{errors[category.categoryName]}</p>
                                                                 )}

                                                                 <p
                                                                      style={{ fontSize: "12px", color: "grey", marginTop: "4px", fontStyle:"italic"}}
                                                                 >
                                                                 This is a combined AI-generated response to all the questions listed above.
                                                                 </p>

                                                                 <div>
                                                                      <Button
                                                                           color="primary"
                                                                           rawProps={{ "data-testid": "re-generate-btn" }}
                                                                           caption="Re-generate AI"
                                                                           onClick={() => {
                                                                                setIsOpen(true);
                                                                                const currentCategoryName = category.categoryName;
                                                                                setRegenerateCategoryName(currentCategoryName);
                                                                           }}
                                                                      />
                                                                      {isOpen && (
                                                                           <div style={{
                                                                                position: "fixed",
                                                                                top: 0,
                                                                                left: 0,
                                                                                right: 0,
                                                                                bottom: 0,
                                                                                zIndex: 1000,
                                                                                display: "flex",
                                                                                justifyContent: "centre",
                                                                                alignItems: "center",
                                                                                pointerEvents: "auto"

                                                                           }}>
                                                                                <ModalBlocker
                                                                                     disableCloseByEsc={true}
                                                                                     disallowClickOutside={true}
                                                                                     style={{ pointerEvents: "auto" }}

                                                                                >
                                                                                     <ModalWindow>
                                                                                          <Panel background="surface-main">
                                                                                               <ModalHeader
                                                                                                    title="Regenerate AI Response"
                                                                                                    rawProps={{
                                                                                                         style: { paddingBottom: "5px" },
                                                                                                    }}
                                                                                                    borderBottom={true}
                                                                                                    onClose={() => {
                                                                                                         setIsOpen(false);
                                                                                                    }}
                                                                                               />
                                                                                               <ScrollBars hasTopShadow hasBottomShadow>
                                                                                                    <FlexRow padding="24" alignItems="center">
                                                                                                         <div style={{ marginBottom: "20px" }}>
                                                                                                              <p
                                                                                                                   style={{fontSize: "16px",
                                                                                                                        fontWeight: "600",
                                                                                                                        margin: "10px 0 8px 0",
                                                                                                                   }}
                                                                                                              >
                                                                                                                   Please note:
                                                                                                              </p>

                                                                                                              <p
                                                                                                                   style={{
                                                                                                                        fontSize: "16px",
                                                                                                                        lineHeight: "1.5",
                                                                                                                        margin: "0 0 12px 0",
                                                                                                                   }}
                                                                                                              >
                                                                                                                   Along with this response, the following fields will also be
                                                                                                                   updated based on the new AI-generated content:
                                                                                                              </p>

                                                                                                              <ul
                                                                                                                   style={{
                                                                                                                        paddingLeft: "24px",
                                                                                                                        margin: "0",
                                                                                                                        fontSize: "16px",
                                                                                                                        lineHeight: "1.6",
                                                                                                                   }}
                                                                                                              >
                                                                                                                   <li>
                                                                                                                        <span style={{ fontWeight: "600" }}>
                                                                                                                             Expected Time to Readiness
                                                                                                                        </span>
                                                                                                                   </li>
                                                                                                                   <li>
                                                                                                                        <span style={{ fontWeight: "600" }}>
                                                                                                                             Opportunities & Business Problems Solved
                                                                                                                        </span>
                                                                                                                   </li>
                                                                                                                   <li>
                                                                                                                        <span style={{ fontWeight: "600" }}>
                                                                                                                             Estimated Budget Requirement Per Person
                                                                                                                        </span>
                                                                                                                   </li>
                                                                                                              </ul>
                                                                                                         </div>
                                                                                                    </FlexRow>
                                                                                               </ScrollBars>
                                                                                               <ModalFooter
                                                                                                    rawProps={{
                                                                                                         style: { justifyContent: "end" },
                                                                                                    }}
                                                                                               >
                                                                                                    <Button
                                                                                                         color="secondary"
                                                                                                         fill="outline"
                                                                                                         caption="Cancel"
                                                                                                         onClick={() => {
                                                                                                              setIsOpen(false);
                                                                                                         }}
                                                                                                    />
                                                                                                    <Button
                                                                                                         color="accent"
                                                                                                         caption="Continue"
                                                                                                         onClick={() => {
                                                                                                              generateSingleCategoryResponse(regenerateCategoryName);
                                                                                                              setIsOpen(false);
                                                                                                         }}
                                                                                                    />
                                                                                               </ModalFooter>
                                                                                          </Panel>
                                                                                     </ModalWindow>
                                                                                </ModalBlocker>
                                                                           </div>
                                                                      )}
                                                                 </div>
                                                            </div>
                                                       </Accordion>
                                                  );
                                             }
                                        })}
                                        {formData.categories.map((category, index) => {
                                             if (category.categoryName === "Future Skills (3–5 years)") {
                                                  return (
                                                       <LabeledInput
                                                            label={
                                                                 <>
                                                                      {category.categoryName}
                                                                      <span style={{ color: "red", marginLeft: "5px" }}>*</span>
                                                                 </>
                                                            }
                                                            key={index}

                                                       >
                                                            <div
                                                                 style={{
                                                                      display: "flex",
                                                                      gap: "10px",
                                                                      alignItems: "center",
                                                                 }}
                                                            >
                                                                 <TextInput
                                                                      value={currentFutureSkill}
                                                                      onValueChange={setCurrentFutureSkill}
                                                                      placeholder="Enter future skill"
                                                                      isDisabled={disableFields || isSaved}

                                                                 />
                                                                 <Button
                                                                      onClick={handleAddFutureSkill}
                                                                      fill="accent"
                                                                      caption="Add Skill"
                                                                      size="30"
                                                                      isDisabled={disableFields || isSaved}
                                                                 />
                                                            </div>
                                                            {errors[category.categoryName] && (
                                                                 <p style={{ color: "red" }}>{errors[category.categoryName]}</p>
                                                            )}
                                                            {futureSkillsList.length > 0 && (
                                                                 <div
                                                                      style={{
                                                                           display: "flex",
                                                                           flexWrap: "wrap",
                                                                           gap: "8px",
                                                                           marginTop: "8px",
                                                                      }}
                                                                 >
                                                                      {futureSkillsList.map((skill) => (
                                                                           <Tag
                                                                                caption={skill}
                                                                                onClear={() => handleRemoveFutureSkill(skill)}
                                                                                key={skill}
                                                                                isRemovable={!(disableFields || isSaved)}
                                                                           >
                                                                                {skill}
                                                                           </Tag>
                                                                      ))}
                                                                 </div>
                                                            )}
                                                       </LabeledInput>
                                                  );
                                             }
                                        })}
                                        {formData.categories.map((category, index) => {
                                             if (!category.questions || category.questions.length === 0 && category.categoryName !== "Future Skills (3–5 years)") {
                                                  return (
                                                       <LabeledInput
                                                            label={
                                                                 <>
                                                                      {category.categoryName}
                                                                      <span style={{ color: "red", marginLeft: "5px" }}>*</span>
                                                                 </>
                                                            }
                                                            key={index}
                                                       >
                                                            <div style={{ display: "flex", gap: "10px", alignItems: "center" }}>
                                                                 <div style={{ width: "100%" }}>
                                                                      <FlexRow spacing="12">
                                                                           <FlexCell grow={1}>
                                                                                <TextArea
                                                                                     value={
                                                                                          AIMap.size > 0
                                                                                               ? AIMap.get(category.categoryName)
                                                                                               : "AI is generating a response for you..."
                                                                                     }
                                                                                     onValueChange={setUserInput}
                                                                                     placeholder="Answer generated by AI will appear here"
                                                                                     isDisabled={true}
                                                                                     rows={6}
                                                                                />
                                                                           </FlexCell>

                                                                           <FlexCell grow={1}>
                                                                                <TextArea
                                                                                     value={answers[category.categoryName] || ""}
                                                                                     rows={6}
                                                                                     autoFocus={true}
                                                                                     onValueChange={(val) => handleInputChange(category.categoryName, val)}
                                                                                     placeholder="Write Your Response Here"
                                                                                />
                                                                           </FlexCell>
                                                                      </FlexRow>
                                                                 </div>
                                                                 {errors[category.categoryName] && (
                                                                      <p style={{ color: "red" }}>{errors[category.categoryName]}</p>
                                                                 )}

                                                            </div>
                                                            <p style={{ fontSize: "12px", color: "grey", marginTop: "4px", fontStyle:"italic" }}>
                                                                 This is an AI-generated response and may not always be accurate.
                                                            </p>
                                                       </LabeledInput>
                                                  );
                                             }
                                        })}
                                   </FlexCell>
                                   <div className="button-container">
                                        <Button
                                             fill="outline"
                                             color="primary"
                                             size="30"
                                             caption="Save as Draft"
                                             isDisabled={disableDraft}
                                             onClick={() => { handleSubmission("D") }}
                                             rawProps={{ "data-testid": "draft-btn" }} 
                                        />
                                        <Button
                                             color="accent"
                                             size="30"
                                             caption="Submit"
                                             isDisabled={disableSubmit}
                                             onClick={handleSave}
                                             rawProps={{ "data-testid": "submit-btn" }} 
                                        />
                                   </div>
                                   <FormFooter lastupdated={lastupdated} />
                              </div>
                         }
                    </form>
                    {showModal && (
                         <ModalBlocker onClose={CancelSave}>
                              <ModalWindow className="large-modal">
                                   <ModalHeader
                                        title="Confirm Submission"
                                        rawProps={{
                                             style: { paddingBottom: "5px" }
                                        }}
                                        borderBottom={true}
                                        onClose={CancelSave
                                        }
                                   />
                                   <FlexRow padding="24" >
                                        <Text fontSize="18">Do you want to continue?</Text>
                                   </FlexRow>
                                   <ModalFooter className="modal-footer"
                                        rawProps={{
                                             style: { justifyContent: "end" }
                                        }}
                                   >
                                        <Button
                                             color="secondary"
                                             fill="outline"
                                             caption="Cancel"
                                             onClick={CancelSave
                                             }
                                        />
                                        <Button
                                             color="accent"
                                             caption="Continue"
                                             onClick={() => { handleSubmission("S") }}
                                        />
                                   </ModalFooter>
                              </ModalWindow>
                         </ModalBlocker>
                    )}
                    <Alert />
               </div>
          </div>
     );
}

export default FutureSkills;