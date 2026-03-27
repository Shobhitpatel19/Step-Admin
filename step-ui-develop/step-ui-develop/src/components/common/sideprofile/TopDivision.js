
import React from 'react';
import "../../styling/side-profile.css";
import { ReactComponent as TelescopeFillIcon } from "@epam/assets/icons/internal_logo/telescope-fill.svg";
const TopDivision = (props) => {

  const profileLink = `https://telescope.epam.com/who/key/upsaId/${props.data.employmentId}?tab=profile`;

  return (
    <div className='top'>
      < div className='top-1'>

        <p className='name'> {props.data.fullName}
        </p>
       
      </div>
      <div className='top-2'>
        <img alt='profile image' className='image' src={props.data.photo}></img>
      </div>
      <div className='top-3'>
        <div className='designation-box' >
          <div>
            <p className='designation' style={{ textTransform: "uppercase" }} >{props.data.jobDesignation}</p>
          </div>
        </div>
        <div className='address-box' >
          <p className='address'>{props.data.officeAddress}</p>
          <a href={profileLink} target="_blank"><TelescopeFillIcon  /></a>

        </div>
      </div>
    </div>
  )
}

export default TopDivision;
