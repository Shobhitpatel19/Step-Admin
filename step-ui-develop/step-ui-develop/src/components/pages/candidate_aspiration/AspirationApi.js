import axiosInstance from "../../common/axios";

export const fetchAspirationsList = async () => {
  return axiosInstance.get("step/aspirations");
};

export const fetchAspirationDescription = async () => {
  try {
    const response = await axiosInstance.get("step/aspirations/NA");
    console.log(response.data, "from fetch description");
    return response.data;
  } catch (error) {
    console.error("Error fetching aspiration description:", error);
    throw error;
  }
};

export const fetchAspirationByPriority = async (priority) => {
  console.log("called by priority in aspirations priority" + priority);

  try {
    const response = await axiosInstance.get(`step/aspirations/${priority}`);
    console.log(
      response.data.aspirationList,
      "fetching aspirations data response"
    );
    return response.data;
  } catch (error) {
    console.error("Error fetching aspiration by priority:", error);
    throw error;
  }
};

export const updateAspiration = async (priority, aspirationData) => {
  const priorityCode = priority === "Primary" ? "P1" : "P2";
  console.log("priority in update aspirations" + priorityCode);
  try {
    const response = await axiosInstance.put(
      `/step/aspirations/${priorityCode}`,
      aspirationData,
      {
        headers: {
          "Content-Type": "application/json",
        },
      }
    );
    return response.data;
  } catch (error) {
    console.log(aspirationData);
    console.error("Error submitting aspiration:", error);
    throw error;
  }
};

export const submitAspiration = async (formData) => {
  try {
    const response = await axiosInstance.post("/step/aspirations", formData, {
      headers: {
        "Content-Type": "application/json",
      },
    });
    return response.data;
  } catch (error) {
    console.log(formData);

    console.error("Error submitting aspiration:", error);
    throw error;
  }
};

export const deleteAspiration = async (priority) => {
  const priorityCode = priority === "Primary" ? "P1" : "P2";

  try {
    const response = await axiosInstance.delete(
      `/step/aspirations/${priorityCode}`
    );
    console.log(
      `Aspiration with priority ${priority} (${priorityCode}) deleted successfully`
    );
    return response.data;
  } catch (error) {
    console.error(
      `Error deleting aspiration with priority ${priority} (${priorityCode}):`,
      error
    );
    throw error;
  }
};
