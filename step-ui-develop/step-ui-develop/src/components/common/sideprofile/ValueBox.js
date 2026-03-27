import React from 'react'

const ValueBox = (props) => {
  
  var className = "rounded-box-1";
  if(props.value === "Not Active")
  {
    className = "rounded-box-2";
  }
  if(props.value === "Active"){
    className = "rounded-box-3";
  }
  
  return (
    <div className='value-box' >
        <div className={className} >
            <p data-testid = "value-box-test" className='detail-paragraph-value'>
                {props.value}
            </p>
        </div>
    </div>
  )
}
export default ValueBox;
