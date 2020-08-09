export const searchUsers = async (search, limit, token) => {
    const url = `http://local.courier.net:8080/api/v1/search/searchUsers?q=${search}&limit=${limit}`;
    return await fetch(url, {
        method: "GET",
        credentials: "include",
        headers: {
            "Authorization": `Bearer ${token}`,
            "Content-Type": "application/json"
        }
    })
    .then(response => {
        if(response.status !== 200) return [];
        return response.json();
    })
    .then(json => {
        return json;
    })
    .catch(error => {
        console.log(error);
        return [];
    })
}

export const stripBadChars = (search) => {
    return search.replace(/[\]{}()\\<>*+?|:=!^-]/gi, '');
}
