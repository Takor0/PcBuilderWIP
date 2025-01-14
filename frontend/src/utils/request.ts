export const request = async ({
                                  url,
                                  query,
                                  body,
                                  method = 'GET',
                                  formData,
                              }: {
    url: string;
    query?: Record<string, string>;
    body?: unknown;
    method?: string;
    formData?: FormData;

}) => {
    const options: RequestInit = {
        method,
        // credentials: 'include',
    };
    const headers: HeadersInit = {};
    const jsonMethods = ['POST', 'PUT', 'PATCH', 'DELETE'];
    if (body && jsonMethods.includes(method) && !headers['Content-Type']) {
        headers['Content-Type'] = 'application/json';
    }

    if (headers) {
        options.headers = headers;
    }


    if (body && typeof body === 'object') {
        options.body = JSON.stringify(body);
    }
    if (formData) {
        options.body = formData;
    }

    if (query) {
        const queryString = new URLSearchParams(query).toString();
        url += `?${queryString}`;
    }
    const res = await fetch(url, options);
    const contentType = res.headers.get('content-type');
    if (contentType !== 'application/json' || res.status === 204) {
        return {
            res,
            data: {},
            pagination: {},
            headers: res.headers,
        };
    }


    const data = await res.json();
    return {
        res,
        data,
        headers: res.headers,
    };
};