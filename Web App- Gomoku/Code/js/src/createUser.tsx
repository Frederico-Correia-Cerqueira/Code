import React from 'react'
import {UserManagement} from "./userManagement"
import {NavBarTop} from "./elements/navBar";
import {Link} from "react-router-dom";

export function CreateUser() {
    return (
        <div>
            <NavBarTop elements={[
                { children: <Link className='start' to='/'>Home</Link>, position: 'start'},
                { children: <h2 className='center'>Create User</h2>}
                
            ]}/>
            <div style={{
            height: '100vh',
            width: '100vw',
            marginBlock: '15%',
            marginInline: 'auto'
            
        }}>
            <UserManagement
                uri={"api/players"}
                msg={"Player already exists"}
                buttonName={"Create User"}
                link={"/login"}
                linkMessage={"Login"}
            />
            </div>
        </div>
    )
}

