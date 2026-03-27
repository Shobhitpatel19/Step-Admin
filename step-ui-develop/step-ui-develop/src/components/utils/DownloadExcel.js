import * as XLSX from 'xlsx-js-style';

const DownloadExcelTemplate = (content, mandatoryColumns, fileName) => {

  console.log(mandatoryColumns);
  const wb = XLSX.utils.book_new();
  const ws = XLSX.utils.aoa_to_sheet(content);

  const range = XLSX.utils.decode_range(ws['!ref']);
  for (let C = range.s.c; C <= range.e.c; C++) {
    console.log("BNM<")
    const cellAddress = XLSX.utils.encode_cell({ r: 0, c: C });
    const cell = ws[cellAddress];

    if (cell && mandatoryColumns.includes(cell.v)) {
      cell.s = {
        fill: { fgColor: { rgb: "FFFF00" } },
      };
    }
  }

  XLSX.utils.book_append_sheet(wb, ws, 'Template');

  wb.Props = {
    Title: "Data Template",
    Subject: "Template",
    Author: "EPAM",
    CreatedDate: new Date()
  };

  XLSX.writeFile(wb, fileName + '.xlsx');
  
  return true;
};

export default DownloadExcelTemplate;
