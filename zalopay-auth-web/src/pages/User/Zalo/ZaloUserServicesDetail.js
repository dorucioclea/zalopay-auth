import React, { PureComponent } from 'react';
import {List, Tag, Col, Avatar, message, Select, Button, Row, Form} from 'antd';
import { connect } from 'dva';
import styles from './ZaloUserServicesDetail.less';

const FormItem = Form.Item;
const { Option } = Select;

@connect(({ zaloUsers, services }) => ({
  zaloUsers, services
}))
class ZaloUserServicesDetail extends PureComponent {

  state = {
    newService: '',
    cities: [],
    secondCity: '',
  };

  handleDeleteUserRole(roles) {
    dispatch({
      type: 'zaloUsers/query',
      payload: {
        roles: roles
      },
    });
  };

  handleDeleteUserRole = (clientId, username, role) => {
    const {
      zaloUsers: {
        queryUser: {
          realmId
        }
      },
      dispatch,
    } = this.props;
    dispatch({
      type: 'zaloUsers/deleteClientRoles',
      payload: {
        clientId: clientId,
        userId: realmId,
        userName: username,
        roles: role,
      },
    });

    message.success(`Role ${role.split('___')[1]} removed`);
  };

  handleProvinceChange = (value) => {
    const {
      services: { list },
    } = this.props;
    const currentService = list.filter(service => service.id === value);
    this.setState({
      newService: currentService[0].id,
      cities: currentService[0].roles,
      secondCity: currentService[0].roles[0],
    });
  };

  onSecondCityChange = (value) => {
    this.setState({
      secondCity: value,
    });
  };

  handleAddRolesToUser = () => {
    const {
      zaloUsers: {
        queryUser: {
          realmId,
          username,
        }
      },
      dispatch,
    } = this.props;

    const {
      secondCity,
      newService
    } = this.state;
    const roles = [secondCity];

    dispatch({
       type: 'zaloUsers/updateClientRoles',
       payload: {
         userName:username,
         userId: realmId,
         clientId: newService,
         roles: roles,
       },
     });
  };

  render() {
    const {
      zaloUsers: {
        queryUser : {
          services
        }
      },
      services: { list },
    } = this.props;

    const {
      cities,
      secondCity,
      newService
    } = this.state;

    const AddRoleToService = () => (
      <Row gutter={{ md: 8, lg: 24, xl: 48 }}>
        <Col md={8} sm={24}>
          <FormItem label="Service Name">
            <Select
              defaultValue={newService}
              style={{ width: 200 }}
              onChange={this.handleProvinceChange}
            >
              {list.map(province => <Option key={province.id}>{province.title}</Option>)}
            </Select>
          </FormItem>
        </Col>
        <Col md={8} sm={24}>
          <FormItem label="Service Roles">
            <Select
              style={{ width: 200 }}
              value={secondCity}
              onChange={this.onSecondCityChange}
            >
              {cities.map(city => <Option key={city}>{city}</Option>)}
            </Select>
          </FormItem>
        </Col>
        <Col md={8} sm={24}>
          <FormItem label="Action">
            <span className={styles.submitButtons}>
              <Button type="primary" onClick={this.handleAddRolesToUser}>
                Add Role
              </Button>
              <Button style={{ marginLeft: 8 }} onClick={this.handleFormReset}>
                Reset
              </Button>
            </span>
          </FormItem>
        </Col>
      </Row>
    );


    return (
      <List
        size="large"
        className={styles.articleList}
        rowKey="id"
        itemLayout="vertical"
        header={<AddRoleToService />}
        dataSource={services}
        renderItem={item => (
          item.serviceRoles && Object.keys(item.serviceRoles).length ? (
            <List.Item
              key={item.serviceName}
              // actions={[
              //   <IconText type="plus-o" text="Add Role" />
              // ]}
            >
              <List.Item.Meta
                avatar={
                  <Avatar src={item.serviceLogo} />
                }
                title={
                  <a className={styles.listItemMetaTitle} href={item.serviceUrl}>
                    {item.serviceName}
                  </a>
                }
                description={
                  (item.serviceRoles.map(role => (
                    <Tag key={role} closable onClose={() => this.handleDeleteUserRole(item.serviceRealmId, item.username, role)}>{role.split('___')[1]}</Tag>
                  )))
                }
              />
            </List.Item>
          ) : (<div /> )
        )}
      />
    );
  }
}

export default ZaloUserServicesDetail;
