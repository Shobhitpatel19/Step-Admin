import DownloadExcelTemplate from "../components/utils/DownloadExcel"; // Adjust path if needed
import * as XLSX from "xlsx-js-style";

jest.mock("xlsx-js-style", () => {
  const originalModule = jest.requireActual("xlsx-js-style");
  return {
    ...originalModule,
    utils: {
      book_new: jest.fn(),
      aoa_to_sheet: jest.fn(),
      book_append_sheet: jest.fn(),
      writeFile: jest.fn(),
      decode_range: jest.fn(),
      encode_cell: jest.fn(),
    },
    writeFile: jest.fn(), // This is the important fix!
  };
});

describe("DownloadExcelTemplate", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    XLSX.writeFile.mockImplementation(() => {}); // Prevents 'Invalid Workbook'
  });

  it("should apply style to mandatory column headers and export Excel", () => {
    const content = [
      ["Name", "Email", "Age"],
      ["John", "john@example.com", 30],
    ];
    const mandatoryColumns = ["Name", "Email"];
    const fileName = "TestTemplate";

    const ws = {
      A1: { v: "Name" },
      B1: { v: "Email" },
      C1: { v: "Age" },
      "!ref": "A1:C2",
    };

    const wb = {
      SheetNames: [],
      Sheets: {},
      Props: {},
    };

    XLSX.utils.book_new.mockReturnValue(wb);
    XLSX.utils.aoa_to_sheet.mockReturnValue(ws);
    XLSX.utils.decode_range.mockReturnValue({ s: { c: 0 }, e: { c: 2 } });
    XLSX.utils.encode_cell.mockImplementation(({ r, c }) => {
      const col = String.fromCharCode(65 + c);
      return `${col}${r + 1}`;
    });

    const result = DownloadExcelTemplate(content, mandatoryColumns, fileName);

    expect(XLSX.utils.book_new).toHaveBeenCalled();
    expect(XLSX.utils.aoa_to_sheet).toHaveBeenCalledWith(content);
    expect(XLSX.utils.decode_range).toHaveBeenCalledWith("A1:C2");
    expect(XLSX.utils.encode_cell).toHaveBeenCalledTimes(3);
    expect(ws.A1.s).toEqual({ fill: { fgColor: { rgb: "FFFF00" } } });
    expect(ws.B1.s).toEqual({ fill: { fgColor: { rgb: "FFFF00" } } });
    expect(ws.C1.s).toBeUndefined();
    expect(XLSX.utils.book_append_sheet).toHaveBeenCalledWith(
      wb,
      ws,
      "Template"
    );
    expect(XLSX.writeFile).toHaveBeenCalledWith(wb, "TestTemplate.xlsx");
    expect(result).toBe(true);
  });

  it("should not apply style if no headers match mandatoryColumns", () => {
    const content = [["Name", "Email", "Age"]];
    const mandatoryColumns = ["Address"];
    const fileName = "NoStyle";

    const ws = {
      A1: { v: "Name" },
      B1: { v: "Email" },
      C1: { v: "Age" },
      "!ref": "A1:C1",
    };

    const wb = {
      SheetNames: [],
      Sheets: {},
      Props: {},
    };

    XLSX.utils.book_new.mockReturnValue(wb);
    XLSX.utils.aoa_to_sheet.mockReturnValue(ws);
    XLSX.utils.decode_range.mockReturnValue({ s: { c: 0 }, e: { c: 2 } });
    XLSX.utils.encode_cell.mockImplementation(({ r, c }) => {
      const col = String.fromCharCode(65 + c);
      return `${col}${r + 1}`;
    });

    const result = DownloadExcelTemplate(content, mandatoryColumns, fileName);

    expect(ws.A1.s).toBeUndefined();
    expect(ws.B1.s).toBeUndefined();
    expect(ws.C1.s).toBeUndefined();
    expect(XLSX.writeFile).toHaveBeenCalledWith(wb, "NoStyle.xlsx");
    expect(result).toBe(true);
  });
});
