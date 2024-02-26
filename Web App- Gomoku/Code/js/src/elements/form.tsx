import React from "react";
import '../../css/form.css'

export function Form({input1, label1, input2, label2, error, msg, changeHandlerInput1, changeHandlerInput2, clickHandler, buttonName, secretInput = true}) {
    return (<div>
        <label className="labelForm">{label1}</label>
        <input type="text" className="input-styled" value={input1} onChange={changeHandlerInput1}/>

        <br/>

        <label className="labelForm">{label2}</label>
        <input type={secretInput ? "password" : "text"} className="input-styled" autoComplete="off" value={input2} onChange={changeHandlerInput2}/>

        <br/>
        <Error error={error} msg={msg}/>
        <button className="buttonForm" onClick={clickHandler}>{buttonName}</button>
    </div>)
}

function Error({error, msg}: { error: boolean, msg: string }) {
    if (error) {
        return (
            <div className="error">
                {msg}
            </div>
        )
    } else {
        return (
            <div>
            </div>
        )
    }
}