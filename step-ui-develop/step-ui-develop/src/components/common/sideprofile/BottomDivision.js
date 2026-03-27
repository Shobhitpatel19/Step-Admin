import React from 'react';
import "../../styling/side-profile.css";
import DetailBox from './DetailBox';

const BottomDivision = (props) => {
  return (
    <div className='bottom'>  
      {Object.entries(props.data).map(([key, value], index) => (
        <DetailBox key={index} keyName={key} value={value} />
      ))}
    </div>
  )
}
export default BottomDivision;