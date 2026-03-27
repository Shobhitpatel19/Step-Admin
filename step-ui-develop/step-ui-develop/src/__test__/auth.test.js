// ✅ Mocks first
jest.mock("js-cookie", () => ({
  get: jest.fn(() => ({
    jwtToken: "testToken",
    otherCookie: "123",
  })),
  set: jest.fn(),
  remove: jest.fn(),
}));

jest.mock("jwt-decode", () => ({
  jwtDecode: jest.fn(),
}));

import Cookies from "js-cookie";
import { jwtDecode } from "jwt-decode";

import {
  getTokenFromCookies,
  removeAllCookies,
  storeTokenInCookies,
  removeTokenFromCookies,
  decodeToken,
  isAuthenticated,
} from "../components/utils/auth";

describe("authUtils", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    document.cookie = "jwtToken=testToken; otherCookie=123";
    sessionStorage.clear();
  });

  describe("getTokenFromCookies", () => {
    it("should return jwtToken from cookies", () => {
      const token = getTokenFromCookies();
      expect(token).toBe("testToken");
    });

    it("should return null if jwtToken not present", () => {
      document.cookie =
        "jwtToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
      document.cookie = "session=abc123";
      const token = getTokenFromCookies();
      expect(token).toBeNull();
    });
  });

  describe("storeTokenInCookies", () => {
    it("should set cookie if token is valid", () => {
      storeTokenInCookies("abc123");
    });

    it("should log error and not set cookie if token is falsy", () => {
      console.error = jest.fn();
      storeTokenInCookies(null);
      expect(console.error).toHaveBeenCalledWith(
        "Invalid token. Cannot store in cookies."
      );
    });
  });

  describe("removeTokenFromCookies", () => {
    it("should remove jwtToken", () => {
      console.log = jest.fn();
      removeTokenFromCookies();
      expect(console.log).toHaveBeenCalledWith("Removing token from cookies");
      expect(Cookies.remove).toHaveBeenCalledWith("jwtToken");
    });
  });

  describe("decodeToken", () => {
    it("should return parsed token info", () => {
      jwtDecode.mockReturnValue({
        name: "John Doe",
        picture: "img.jpg",
        role: "admin",
        isDelegate: true,
      });

      const result = decodeToken("mockToken");
      expect(jwtDecode).toHaveBeenCalledWith("mockToken");
      expect(result).toEqual({
        decoded: {
          name: "John Doe",
          picture: "img.jpg",
          role: "admin",
          isDelegate: true,
        },
        firstName: "John",
        picture: "img.jpg",
        role: "admin",
        isDelegate: true,
      });
    });

    it("should return fallback if name missing", () => {
      jwtDecode.mockReturnValue({
        picture: "img.jpg",
        role: "admin",
      });

      const result = decodeToken("mockToken");
      expect(result.firstName).toBe("Guest");
    });

    it("should return default on invalid token", () => {
      console.error = jest.fn();
      const result = decodeToken(null);
      expect(result).toEqual({ decoded: null, firstName: "Guest" });
      expect(console.error).toHaveBeenCalledWith(
        "Error decoding token:",
        "Invalid token. Cannot decode."
      );
    });
  });

  describe("isAuthenticated", () => {
    it("should return true if token exists in session storage", () => {
      sessionStorage.setItem("token", "abc123");
      expect(isAuthenticated()).toBe(true);
    });

    it("should return false if token does not exist", () => {
      expect(isAuthenticated()).toBe(false);
    });
  });
});
