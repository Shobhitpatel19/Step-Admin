
const initialState = {
    notifyStatus : null,
    notifyMessage : null,
    isSuccess : null,
};

const notificationreducer = (state = initialState, action) => {
    switch(action.type)
    {
        case "NOTIFY":
            return {
                ...state,
                notifyStatus : true,
                notifyMessage : action.payload.notifyMessage,
                isSuccess : action.payload.isSuccess
            };
        case "CANCEL":
            return {
                ...state,
                notifyStatus : false,
            };

        case "RESET_NOTIFY":
            return{
                ...state,
                notifyStatus : false,
            }

        default:
            return state;
    }
}

export default notificationreducer;