import React from 'react'

function SearchBar(props) {

    const searchTriggered = (e) => {
        e.preventDefault();
        const searchText = e.target.elements.search.value.trim();
        console.log("Triggering unimplemented search call...");
        console.log(searchText);
        e.target.elements.search.value = null;
    }

    const somethingTyped = async (e) => {
        // implement something related to autocomplete here
    }

    return (
        <div className="p-1 pb-2">
            <form onSubmit={searchTriggered}>
                <input onKeyDown={somethingTyped} name="search" className="form-control input-hover rounded-pill" type="text" placeholder="Search chats..."/>
            </form>
        </div>
    )

}

export default SearchBar;