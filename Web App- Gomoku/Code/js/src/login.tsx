import React from 'react'
import {UserManagement} from "./userManagement";
import {Link, useNavigate} from "react-router-dom";
import {fetchGet} from "./fetch";
import {isUserLogged} from "./elements/validateUser";
import {NavBarTop} from "./elements/navBar";

export function Login() {
    const navigate = useNavigate()

    fetchGet("/api/getAuthCookie")
        .then(text => {
                if (text != undefined) {
                    const jsonData = JSON.parse(text)
                    if (jsonData.properties != undefined) {
                        if (jsonData.properties.present) {
                            navigate("/")
                        }
                    }
                }
            }
        )
    return (
        <div >
            <NavBarTop elements={[
                { children: <Link className='start' to='/'>Home</Link>, position: 'start' },
                { children: <h2 className='center'>Login</h2>, position: 'center' }
            ]}/>
            <div style={{
                 height: '100vh',
                width: '100vw',
                marginBlock: '15%',
                marginInline: 'auto'
            }}>
            <UserManagement
                uri={"api/login"}
                msg={"Wrong username or password"}
                buttonName={"Login"}
                link={"/createUser"}
                linkMessage={"Don't have an account? Sign in!"}
            />
            </div>
        </div>
    )
}
