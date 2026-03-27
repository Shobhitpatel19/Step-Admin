import React, { useState, useEffect, useMemo } from 'react';
import css from '../../styling/admin_notification_page/NotificationPage.module.css';
import { Accordion, SearchInput, Button, Switch } from '@epam/uui';
import { ReactComponent as NavigationCloseFillIcon } from '@epam/assets/icons/navigation-close-fill.svg';
import axiosInstance from "../../common/axios";
import { notify } from "../../../redux/actions";
import { useDispatch } from "react-redux";
import Alert from '../../common/Alert';
import { useNavigate } from "react-router-dom";


const NotificationPage = (props) => {
  const [searchValue, setSearchValue] = useState('');
  const [practiceHeadNotifications, setPracticeHeadNotifications] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    fetchNotificationsData();
  }, []);
  const fetchNotificationsData = async () => {
    setIsLoading(true);
    try {
      const response = await axiosInstance.get('/step/notifications');
      if (response.status === 200) {
        const data = await response.data;
        setPracticeHeadNotifications(data);
        setIsLoading(false);
      } else {
        console.error('Failed to fetch data:', response.status);
        setIsLoading(false);
      }
    } catch (error) {
      console.error('Error fetching data:', error);
      setIsLoading(false);
    }
  };
  const handleToggleAll = (ele) => {
    const flag = ele.features.every((feature) => feature.notificationsEnabled);
    sendData(ele.uuid, !flag);
  };
  const handleToggle = async (ele, notification) => {
    const payload = {
      userId: ele.uuid,
      categoryId: notification.categoryId,
      enable: !notification.notificationsEnabled
    };
    try {
      const response = await axiosInstance.patch('/step/notifications',
        JSON.stringify(payload),
        {
          headers: {
            'Content-Type': 'application/json',
          }
        }
      );
      dispatch(notify(`${notification.categoryName} Notifications Updated Successfully`, true));
      updateNotificationsData(response.data);
    } catch (error) {
      console.error('Error updating notification:', error);
    }
  };
  const updateNotificationsData = (updatedObject) => {
    setPracticeHeadNotifications(prevData => {
      return prevData.map(obj => {
        if (obj.uuid === updatedObject.uuid) {
          return updatedObject;
        }
        return obj;
      });
    });
  };
  const sendData = async (uid, flag) => {
    try {
      const response = await axiosInstance.patch(`/step/notifications/${uid}?enable=${flag}`);
      updateNotificationsData(response.data);
      if (response.status !== 200) {
        console.error('Failed to send data:', response.status);
      }
      else {
        dispatch(notify("Notifications Updated Successfully", true));
      }
    } catch (error) {

      dispatch(notify(error.response.data.errorMessage, false));
      console.error('Error sending data:', error);
    }
  };
  const filteredPracticeHeads = useMemo(() => {
    if (!searchValue) {
      return practiceHeadNotifications;
    }
    const lowerCaseSearchValue = searchValue.toLowerCase();
    return practiceHeadNotifications.filter(ele =>
      ele.firstName.toLowerCase().includes(lowerCaseSearchValue) ||
      ele.lastName.toLowerCase().includes(lowerCaseSearchValue) ||
      ele.practice.toLowerCase().includes(lowerCaseSearchValue)
    );
  }, [practiceHeadNotifications, searchValue]);
  return (
    <div className= {css["notification-main-div"]}>
      <Alert></Alert>
      <div className= {css["header-div"]} >
        <div className={css["side-panel-header"]}>
          <h4>Notification Management</h4>
          <div style={{ position: 'absolute', top: '2.5%', right: '3%' }}>
            <NavigationCloseFillIcon
              color='white'
              onClick={() => {
                props.closeMethod(false);
                navigate("/welcome");
              }}
            />
          </div>
        </div>
      </div>
      <div className= {css["info-and-filter-div"]}>
        <div className= {css["Text-box"]}  style={{ textAlign: "center" }} >
          <p style={{ fontSize: "12.5px" }} >
            Customize Alert Experience: Practice Head Notification Settings</p>
        </div>
        <div style={{ width: '85%' }}>
          <SearchInput
            value={searchValue}
            onValueChange={setSearchValue}
            placeholder="Search by Name or Practice"
            debounceDelay={300}
          />
        </div>
      </div>
      <div className={css["container"]}>
        <div className={css["content-div"]}>
          {filteredPracticeHeads.map((ele) => (
            <div style={{ "width": "100%", "marginTop": "1%", "fontSize": "1px" }} >
              <Accordion
                key={ele.firstName + " " + ele.lastName}
                title={ele.firstName + " " + ele.lastName}
                rawProps={{ style: { marginTop: "2%", fontSize: "16px" } }}
              >
                <div className={css["notification-header-buttons"]} style={{ display: 'flex', flexWrap: 'wrap' }}>
                  <Button
                    rawProps={{ "data-testid": "enable-disable-button" }}
                    fill="ghost"
                    caption={ele.features.every((feature) => feature.notificationsEnabled) ? 'Disable All' : 'Enable All'}
                    onClick={() => handleToggleAll(ele)}
                    color={ele.features.every((feature) => feature.notificationsEnabled) ? 'critical' : 'accent'}
                  />
                </div>
                <div className={css["practice-head-notifications-grid"]}>
                  {ele.features.map((notification) => (
                    <div key={notification.categoryName}>
                      <Switch

                        rawProps={{ "data-testid": "toggle-button" }}
                        label={notification.categoryName}
                        value={notification.notificationsEnabled}
                        onValueChange={() => handleToggle(ele, notification)}
                      />
                    </div>
                  ))}
                </div>
              </Accordion>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};
export default NotificationPage;