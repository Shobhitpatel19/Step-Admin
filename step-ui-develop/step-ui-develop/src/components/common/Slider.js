import React, { useState, useEffect } from "react";
import { SliderRating } from "@epam/loveship";

export default function Slider({
  value,
  category,
  fieldKey,
  valueCallback,
  isSubmitted,
}) {
  const [sliderValue, setSliderValue] = useState(value);

  const nomenclature = {
    1: "Below expectations",
    2: "Partially meets expectations",
    3: "Meets expectations",
    4: "Exceeds expectations",
    5: "Significantly exceeds expectations",
  };

  useEffect(() => {
    setSliderValue(value);
  }, [value]);

  const handleChange = (v) => {
    setSliderValue(v);
    valueCallback(category, fieldKey, v);
  };

  return (
    <div className="slider">
      <SliderRating
        from={1}
        value={sliderValue}
        onValueChange={handleChange}
        size="18"
        isDisabled={isSubmitted}
        withoutNa={true}
        rawProps={{ "data-testid": "slider-rating" }}
        renderTooltip={(value) => nomenclature[value]}
      />
    </div>
  );
}
