import React from 'react';
import "../../styling/side-profile.css";

function splitAndCapitalize(str) {
  return str.replace(/([a-z])([A-Z])/g, "$1 $2")
            .split(" ")
            .map(word => word.charAt(0).toUpperCase() + word.slice(1)) 
            .join(" "); 
}

const KeyBox = (props) => {
  return (
    <div className='key-box'>
      <p className='detail-paragraph-key'>{splitAndCapitalize(props.keyName)}</p>
    </div>
  )
}
export default KeyBox;