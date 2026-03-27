const initialState = {
  role: "",
};

const practiceratingreducer = (state = initialState, action) => {
  switch (action.type) {
    case "SET_ROLE":
      return {
        // ...state,
        role: action.payload.role,
      };

    default:
      return state;
  }
};

export default practiceratingreducer;
