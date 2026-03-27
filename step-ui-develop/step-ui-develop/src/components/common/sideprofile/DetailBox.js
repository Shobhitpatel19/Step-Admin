import React from 'react'
import KeyBox from './KeyBox';
import ValueBox from './ValueBox';
import "../../styling/side-profile.css";

const DetailBox = (props) => {
    return (
    <div className='detail-box-main' data-testid="detail-box-main">
        <div className='line' data-testid = "separator" >
        </div>
        <div className='details-box'>
            <KeyBox keyName={props.keyName} ></KeyBox>
            <ValueBox value = {props.value} ></ValueBox>
        </div>
    </div>
  )
}
export default DetailBox;