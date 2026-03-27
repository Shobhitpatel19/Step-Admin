module.exports = {
  moduleNameMapper: {
    "\\.(css|less)$": "identity-obj-proxy",
    "^@/(.*)$": "<rootDir>/src/$1",
    "\\.(png|jpe?g|svg|gif)$": "<rootDir>/src/__mock__/fileMock.js",
  },
  
    transformIgnorePatterns: [
      "/node_modules/(?!axios)/" // Allow transforming axios
    ],
 
    moduleNameMapper: {
      "^axios$": require.resolve("axios"), // Ensure axios resolves correctly
    },
   
  transform: {
    "^.+\\.[tj]sx?$": "babel-jest",
  },

  testEnvironment: "jsdom",
  setupFilesAfterEnv: ["<rootDir>/src/setupTests.js"],

  collectCoverage: true,
  collectCoverageFrom: [
    "src/**/*.js",
    "!src/**/*.test.js",
    "!src/**/PracticeRatingDialog.js",
  ],
  coverageDirectory: "coverage",
  coverageThreshold: {
    global: {
      statements: 80,
      branches: 80,
      functions: 80,
      lines: 80,
    },
  },
  coveragePathIgnorePatterns: [
    "C:Userspranavagayathri_aavuDesktopsprint 4\feature_2216step-uisrc__test__PracticeRatingDialog.test.js",
  ],
};
