import React from 'react'

function SearchBar(props) {

    const searchTriggered = (e) => {
        e.preventDefault();
        const searchText = e.target.elements.search.value.trim();
        console.log("Triggering unimplemented search call...");
        if(searchText != null && searchText !== "") {
            console.log(searchText);
        }
        e.target.elements.search.value = null;
    }

    const somethingTyped = async (e) => {
        // implement something related to autocomplete here
    }

    return (
        <div className="flex-grow-1" style={{maxWidth: "80%"}}>
            <form onSubmit={searchTriggered}>
                <input onKeyDown={somethingTyped} name="search" className="form-control input-hover rounded-pill" type="text" placeholder="Search chats..."/>
            </form>
        </div>
    )

}

export default SearchBar;
