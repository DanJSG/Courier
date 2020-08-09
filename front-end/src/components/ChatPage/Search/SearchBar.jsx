import React, {useState} from 'react'

function SearchBar(props) {

    const [searchData] = useState([]);
    const [suggestions, setSuggestions] = useState([]);

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
        if((e.keyCode > 15 && e.keyCode < 19) || e.keyCode === 27 || e.keyCode === 20 || e.keyCode === 91) {
            return;
        }
        const searchText = e.target.value.trim();
        if(searchText == null || searchText === undefined || searchText === '') {
            setSuggestions([]);
            return;
        }
        setSuggestions(() => {
            const regex = new RegExp(`^${searchText}`, 'gi');
            const filtered = searchData.filter(val => val.match(regex));
            return filtered;
        })
    }

    return (
        <div className="flex-grow-1" style={{maxWidth: "80%"}}>
            <form onSubmit={searchTriggered}>
                <input onChange={somethingTyped} name="search" className="form-control input-hover rounded-pill" type="text" placeholder="Search chats..."/>
            </form>
            <div className='flex-grow-1' style={{
                zIndex: 1,
                position: 'absolute',
                backgroundColor: 'white',
                width: '85%',
                maxWidth: '85%'
            }}>
                    {
                        suggestions.map(val => {
                            return <div className="card card-body mb-1" key={Math.random() * 1000}>{val}</div>
                        })
                    }
            </div>
        </div>
    )

}

export default SearchBar;
