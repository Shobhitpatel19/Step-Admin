import { fetchData } from "../components/utils/Api";

describe("fetchData", () => {
  const originalFetch = global.fetch;
  const originalConsoleError = console.error;

  beforeEach(() => {
    console.error = jest.fn(); // Mock console.error
  });

  afterEach(() => {
    global.fetch = originalFetch;
    console.error = originalConsoleError;
    jest.resetAllMocks();
  });

  it("should return data when fetch is successful", async () => {
    const mockResponse = { name: "Test" };
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: true,
        json: () => Promise.resolve(mockResponse),
      })
    );

    const result = await fetchData("https://api.example.com/data");
    expect(fetch).toHaveBeenCalledWith("https://api.example.com/data");
    expect(result).toEqual(mockResponse);
  });

  it("should return null and log error when response is not ok", async () => {
    global.fetch = jest.fn(() =>
      Promise.resolve({
        ok: false,
        status: 500,
      })
    );

    const result = await fetchData("https://api.example.com/data");
    expect(result).toBeNull();
    expect(console.error).toHaveBeenCalledWith(
      new Error("Failed to fetch data")
    );
  });

  it("should return null and log error when fetch throws", async () => {
    const mockError = new Error("Network failure");
    global.fetch = jest.fn(() => Promise.reject(mockError));

    const result = await fetchData("https://api.example.com/data");
    expect(result).toBeNull();
    expect(console.error).toHaveBeenCalledWith(mockError);
  });
});
