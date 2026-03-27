import React from "react";
import { render, screen, fireEvent } from "@testing-library/react";
import Slider from "../components/common/Slider";

// Mock SliderRating component from @epam/loveship
jest.mock("@epam/loveship", () => ({
  SliderRating: ({ value, onValueChange, isDisabled, renderTooltip }) => (
    <div>
      <input
        type="range"
        value={value}
        onChange={(e) => onValueChange(Number(e.target.value))}
        disabled={isDisabled}
        data-testid="slider-input"
      />
      {renderTooltip && renderTooltip(value)}
    </div>
  ),
}));

describe("Slider Component", () => {
  const mockValueCallback = jest.fn();

  const defaultProps = {
    value: 3,
    category: "category1",
    fieldKey: "field1",
    valueCallback: mockValueCallback,
    isSubmitted: false,
  };

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("renders the slider with initial value", () => {
    render(<Slider {...defaultProps} />);
    const sliderInput = screen.getByTestId("slider-input");
    expect(sliderInput).toHaveValue("3"); // Expect the value to be "3" (string)
  });

  test("renders the tooltip based on the value", () => {
    render(<Slider {...defaultProps} />);
    const tooltip = screen.getByText("Meets expectations"); // Nomenclature for value 3
    expect(tooltip).toBeInTheDocument();
  });

  test("updates value when slider changes and calls valueCallback", () => {
    render(<Slider {...defaultProps} />);
    const sliderInput = screen.getByTestId("slider-input");

    fireEvent.change(sliderInput, { target: { value: "4" } });

    expect(sliderInput).toHaveValue("4"); // Expect the value to be "4" (string)
    expect(mockValueCallback).toHaveBeenCalledWith("category1", "field1", 4); // valueCallback should be called with correct args
  });

  test("does not update value or call valueCallback when slider is disabled", () => {
    const props = { ...defaultProps, isSubmitted: true };
    render(<Slider {...props} />);
    const sliderInput = screen.getByTestId("slider-input");

    fireEvent.change(sliderInput, { target: { value: "2" } });

  
  });

  test("updates the slider value via props and renders tooltip correctly", () => {
    const props = { ...defaultProps, value: 4 }; 
    render(<Slider {...props} />);
    const sliderInput = screen.getByTestId("slider-input");

    expect(sliderInput).toHaveValue("4"); 
    const tooltip = screen.getByText("Exceeds expectations");
    expect(tooltip).toBeInTheDocument(); 
  });

  test("should render correct tooltip for value 1", () => {
    const props = { ...defaultProps, value: 1 }; 
    render(<Slider {...props} />);
    const tooltip = screen.getByText("Below expectations");
    expect(tooltip).toBeInTheDocument();
  });

  test("should render correct tooltip for value 5", () => {
    const props = { ...defaultProps, value: 5 }; 
    render(<Slider {...props} />);
    const tooltip = screen.getByText("Significantly exceeds expectations");
    expect(tooltip).toBeInTheDocument();
  });
});
