import React, {} from 'react'

export function FormSaveGame({name, setName, description, setDescription, onSubmitRequest}:
                                 {
                                     name: string,
                                     setName: (s:string) => void,
                                     description: string,
                                     setDescription: (s:string) => void,
                                     onSubmitRequest: () => void
                                 }) {
    return (
        <div>
            <form>
                <label>
                    Name:
                    <input type="text" value={name} onChange={(e) => setName(e.target.value)}/>
                </label>
                <label>
                    Description:
                    <input type="text" value={description}
                           onChange={(e) => setDescription(e.target.value)}/>
                </label>
                <button type="submit" onClick={onSubmitRequest}>Save</button>
            </form>
        </div>
    )
}