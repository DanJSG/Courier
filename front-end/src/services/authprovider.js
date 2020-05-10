const UNAUTHORIZED_JSON = {
    authorized: false,
    id: null,
    displayName: null
};

export const checkAuthorization = async () => {
    const lsToken = localStorage.getItem("acc.tok");
    if(!lsToken) {
      return {
        authorized: false,
        id: null,
        displayName: null
      };
    };
    return await fetch(`http://local.courier.net:8080/api/v1/verifyJwt`, {
      method: "POST",
      credentials: "include",
      headers: {
        "Authorization": `Bearer ${lsToken}`
      }
    })
    .then((response) => {
      console.log("Recieved response...");
      console.log(response);
      if(response.status !== 200) {
        console.log("Bad response");
        return null;
      }
      return response.json();
    })
    .then((json) => {
      if(json === null || json === undefined) {
        return UNAUTHORIZED_JSON;
      }
      return {
        authorized: true,
        id: json.id,
        displayName: json.displayName
      }
    })
    .catch((error) => {
      return UNAUTHORIZED_JSON;
    });
}
