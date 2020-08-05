import requests as req, hashlib, secrets, base64, random, json

OAUTH = {
    'client_id': 'ThpDT2t2EDlO',
    'redirect_uri': 'http://local.courier.net:3000/oauth2/auth_callback',
    'scope': 'name+email',
    'response_type': 'code',
    'audience': 'courier',
    'code_challenge_method': 'S256'
}

def b64_url_encode(value):
    return base64.urlsafe_b64encode(value.encode()).decode()
    
def gen_secure_string(length):
    chars = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
    secure_string = ''
    for _ in range(0, length):
        secure_string += secrets.choice(chars)
    return secure_string

def gen_code_challenge():
    length = random.randint(43, 129)
    code_verifier = gen_secure_string(length)
    hasher = hashlib.new('sha256')
    hasher.update(code_verifier.encode())
    code_challenge = b64_url_encode(str(hasher.hexdigest())).replace('=', '')
    return code_verifier, code_challenge

def gen_state():
    return gen_secure_string(12)

def login(code_challenge, state, email, password):
    url  = 'http://local.courier.net:8090/api/v1/authorize'
    url += '?client_id='            + OAUTH['client_id']
    url += '&redirect_uri='         + OAUTH['redirect_uri']
    url += '&response_type='        + OAUTH['response_type']
    url += '&code_challenge='       + code_challenge
    url += '&state='                + state
    url += '&code_challenge_method=' + OAUTH['code_challenge_method']
    body = {'credentials': base64.b64encode(json.dumps({'email': email,'password': password}).encode()).decode()}
    headers = {'Content-Type': 'application/json'}
    response = req.post(url=url, json=body, headers=headers)
    if response.status_code != 200:
        return None
    return response.json()['code']

def sign_up(email, username, password):
    url = 'http://local.courier.net:8090/api/v1/register'
    details = {
        'email': email,
        'username': username,
        'password': password
    }
    headers = {'Content-Type': 'application/json'}
    response = req.post(url, json=details, headers=headers)
    return True if response.status_code == 200 else False

def request_refresh_token(code, code_verifier, state):
    url  = 'http://local.courier.net:8090/api/v1/token'
    url += '?client_id='        + OAUTH['client_id']
    url += '&redirect_uri='     + OAUTH['redirect_uri']
    url += '&grant_type='       + 'authorization_code'
    url += '&state='            + state
    url += '&code='             + code
    url += '&code_verifier='    + code_verifier
    response = req.post(url)
    if response.status_code != 200:
        return None
    return response.json()['token'], response.cookies.values()[0]

def request_access_token(refresh_token, refresh_cookie):
    url  = 'http://local.courier.net:8090/api/v1/token'
    url += '?client_id='        + OAUTH['client_id']
    url += '&refresh_token='    + refresh_token
    url += '&grant_type='       + 'refresh_token'
    headers = {
        'Authorization': 'Bearer' + refresh_token,
        'Cookie': 'ref.tok='      + refresh_cookie
    }
    response = req.post(url, headers=headers)
    if response.status_code != 200:
        return None
    return response.json()['token'], response.cookies.values()[0]

def authorize(access_token, access_cookie):
    url  = 'http://local.courier.net:8080/api/v1/authorize'
    headers = {
        'Authorization': 'Bearer' + access_token,
        'Cookie': 'acc.tok='      + access_cookie
    }
    response = req.post(url, headers=headers)
    if response.status_code != 200:
        return None
    return response.json()

def main():
    verifier, challenge = gen_code_challenge()
    state = gen_state()
    auth_code = login(challenge, state, 'dan@courier.net', 'password')
    refresh_token, refresh_cookie = request_refresh_token(auth_code, verifier, state)
    access_token, access_cookie = request_access_token(refresh_token, refresh_cookie)
    user = authorize(access_token, access_cookie)
    print(user)

if __name__ == '__main__':
    main()
