import {
    useState,
    useEffect,
} from 'react'

export function useFetchGet({uri}: { uri: string }): string | undefined {
    const [content, setContent] = useState<string>(undefined)

    useEffect(() => {
        let canceled = false

        async function doFetch() {
            const response = await fetch(uri)
            if (canceled) return
            const body = await response.text()
            if (canceled) return
            setContent(body)
        }

        setContent(undefined)
        doFetch().then(() => {
        })
        return () => {
            canceled = true
        }

    }, [])
    return content
}


export function useFetchPost({uri, data}: { uri: string, data: any }): string | undefined {
    const [content, setContent] = useState<string>(undefined)

    useEffect(() => {
        let canceled = false

        async function doPost() {
            const response = await fetch(uri, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            })
            if (canceled) return
            const body = await response.text()
            if (canceled) return
            setContent(body)
        }

        setContent(undefined)
        doPost().then(() => {
        })
        return () => {
            canceled = true
        }

    }, [uri, data])
    return content

}

export async function fetchPost(uri: string, body: Object): Promise<string> {
    let response = await fetch(uri, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    });
    return await response.text();
}

export async function fetchDelete(uri: string, body: Object): Promise<string> {
    let response = await fetch(uri, {
        method: 'DELETE',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(body)
    });
    return await response.text();
}

export async function fetchGet(uri: string) {
    let response = await fetch(uri);
    return await response.text();
}