# Zalopay Auth

This is the probation project at VNG. In this project, we will use React JS, Kong Gateway, Keycloak, Spring boot, JWT...

The project will have following functions:

- Create services
- Update service
- Display users from all services included Role
- Grant/Revoke permission of user from the specific service
- Force log out user
- Disable user
- Tracking request/response to services
- Allow user to access to others services without login require

To achieve these functionalities, we will use following technology stack:

- React JS with Redux Saga for client side development
- Ant.design for UI/UX design
- Spring boot for develop the API server
- Kong for API Gateway
- Keycloak as an Authorization service
- Kong OIDC plugins to allow Kong Gateway have ability to communicate with Keycloak Server
- JWT in access token


## Brief UIs of Zalopay Auth

![Zalopay Auth App](https://github.com/A1Darkwing/zalo-auth/blob/master/documents/Zalo-Auth.gif)

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and 
testing purposes. For more detail please refer to our [wiki](https://github.com/zalopay-oss/zalo-auth/wiki).

### Prerequisites

You will need Docker in order to run this application


### Installing

- Everything has been summed in the docker-compose.yaml file. You just only need to travel to the root folder of the project then run 

```
docker-compose up --build
```

## Document

https://github.com/A1Darkwing/zalo-auth/wiki

## Support

- Any bugs about Zalopay Auth please feel free to report [here](https://github.com/A1Darkwing/zalo-auth/issues).
- And you are welcome to fork and submit pull requests.

## Authors

* **Thanh Tran** - *VNG Employee*

## License

This project is licensed under the [MIT License](https://github.com/A1Darkwing/zalo-auth/blob/master/LICENSE.md)

## Acknowledgments

* Mr Anh LE - Lead Software Engineer at VNG - for extraordinary support
