const FormFooter = ({ lastupdated }) => {
    return (
         <div
              style={{
                   textAlign: "center",
                   marginTop: "20px",
                   fontSize: "20px",
                   color: "red",
              }}
         >
              {lastupdated ? (
                   <p>Last Updated: {new Date(lastupdated).toLocaleString()}</p>

              ) : (
                   <p>No updates yet</p>
              )}
         </div>
    );
};
export default FormFooter;