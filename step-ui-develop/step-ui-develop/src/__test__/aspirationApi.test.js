import axiosInstance from "../components/common/axios";
import {
  fetchAspirationsList,
  fetchAspirationDescription,
  fetchAspirationByPriority,
  updateAspiration,
  submitAspiration,
  deleteAspiration,
} from "../components/pages/candidate_aspiration/AspirationApi";

jest.mock("../components/common/axios", () => ({
  get: jest.fn(),
  post: jest.fn(),
  put: jest.fn(),
  delete: jest.fn(),
}));

describe("Aspiration Service", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe("fetchAspirationsList", () => {
    it("should fetch aspirations list", async () => {
      const mockData = { data: ["item1", "item2"] };
      axiosInstance.get.mockResolvedValue(mockData);

      const result = await fetchAspirationsList();
      expect(result).toEqual(mockData);
    });
  });

  describe("fetchAspirationDescription", () => {
    it("should fetch aspiration description successfully", async () => {
      const mockResponse = { data: { description: "Test description" } };
      axiosInstance.get.mockResolvedValue(mockResponse);

      const result = await fetchAspirationDescription();
      expect(result).toEqual(mockResponse.data);
    });

    it("should handle error when fetching description fails", async () => {
      const error = new Error("Network Error");
      axiosInstance.get.mockRejectedValue(error);

      await expect(fetchAspirationDescription()).rejects.toThrow(
        "Network Error"
      );
    });
  });

  describe("fetchAspirationByPriority", () => {
    it("should fetch aspirations for given priority", async () => {
      const mockResponse = { data: { aspirationList: ["asp1"] } };
      axiosInstance.get.mockResolvedValue(mockResponse);

      const result = await fetchAspirationByPriority("P1");
      expect(result).toEqual(mockResponse.data);
    });

    it("should handle error when fetching aspirations by priority fails", async () => {
      const error = new Error("Fetch error");
      axiosInstance.get.mockRejectedValue(error);

      await expect(fetchAspirationByPriority("P2")).rejects.toThrow(
        "Fetch error"
      );
    });
  });

  describe("updateAspiration", () => {
    it("should update aspiration with Primary priority", async () => {
      const mockData = { success: true };
      const aspirationData = { title: "New Aspiration" };
      axiosInstance.put.mockResolvedValue({ data: mockData });

      const result = await updateAspiration("Primary", aspirationData);
      expect(result).toEqual(mockData);
    });

    it("should update aspiration with Secondary priority", async () => {
      const mockData = { success: true };
      const aspirationData = { title: "Another Aspiration" };
      axiosInstance.put.mockResolvedValue({ data: mockData });

      const result = await updateAspiration("Secondary", aspirationData);
      expect(result).toEqual(mockData);
    });

    it("should handle error on updateAspiration", async () => {
      const error = new Error("Update failed");
      axiosInstance.put.mockRejectedValue(error);
      const aspirationData = { title: "Failed Update" };

      await expect(updateAspiration("Primary", aspirationData)).rejects.toThrow(
        "Update failed"
      );
    });
  });

  describe("submitAspiration", () => {
    it("should submit new aspiration", async () => {
      const formData = { title: "Dream Big" };
      const mockResponse = { data: { id: 1 } };
      axiosInstance.post.mockResolvedValue(mockResponse);

      const result = await submitAspiration(formData);
      expect(result).toEqual(mockResponse.data);
    });

    it("should handle error on submitAspiration", async () => {
      const error = new Error("Submit error");
      const formData = { title: "Fail Submit" };
      axiosInstance.post.mockRejectedValue(error);

      await expect(submitAspiration(formData)).rejects.toThrow("Submit error");
    });
  });

  describe("deleteAspiration", () => {
    it("should delete aspiration with Primary priority", async () => {
      const mockResponse = { data: { success: true } };
      axiosInstance.delete.mockResolvedValue(mockResponse);

      const result = await deleteAspiration("Primary");
      expect(result).toEqual(mockResponse.data);
    });

    it("should delete aspiration with Secondary priority", async () => {
      const mockResponse = { data: { success: true } };
      axiosInstance.delete.mockResolvedValue(mockResponse);

      const result = await deleteAspiration("Secondary");
      expect(result).toEqual(mockResponse.data);
    });

    it("should handle error on deleteAspiration", async () => {
      const error = new Error("Delete failed");
      axiosInstance.delete.mockRejectedValue(error);

      await expect(deleteAspiration("Primary")).rejects.toThrow(
        "Delete failed"
      );
    });
  });
});
