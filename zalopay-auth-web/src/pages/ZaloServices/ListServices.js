import React, { PureComponent } from 'react';
import { findDOMNode } from 'react-dom';
import { connect } from 'dva';
import {
  List,
  Card,
  Input,
  Button,
  Icon,
  Avatar,
  Modal,
  Form,
  Select,
  Tag,
  Switch,
} from 'antd';

import PageHeaderWrapper from '@/components/PageHeaderWrapper';
import Result from '@/components/Result';

import styles from './ListServices.less';

const FormItem = Form.Item;
const SelectOption = Select.Option;
const { Search, TextArea } = Input;

@connect(({ services, loading }) => ({
  services,
  loading: loading.models.list,
}))
@Form.create()
class ListServices extends PureComponent {
  state = { visible: false, done: false };

  formLayout = {
    labelCol: { span: 7 },
    wrapperCol: { span: 13 },
  };

  componentDidMount() {
    const { dispatch } = this.props;
    dispatch({
      type: 'services/fetch',
      payload: {
        count: 5,
      },
    });
  }

  showModal = () => {
    this.setState({
      visible: true,
      current: undefined,
    });
  };

  showEditModal = item => {
    this.setState({
      visible: true,
      current: item,
    });
  };

  handleDone = () => {
    setTimeout(() => this.addBtn.blur(), 0);
    this.setState({
      done: false,
      visible: false,
    });
  };

  handleCancel = () => {
    setTimeout(() => this.addBtn.blur(), 0);
    this.setState({
      visible: false,
    });
  };

  handleSubmit = e => {
    e.preventDefault();
    const { dispatch, form } = this.props;
    const { current } = this.state;
    const id = current ? current.id : '';

    setTimeout(() => this.addBtn.blur(), 0);
    form.validateFields((err, fieldsValue) => {
      if (err) return;
      this.setState({
        done: true,
      });
      dispatch({
        type: 'services/submit',
        payload: {
          id,
          type: 'updateClient',
          ...fieldsValue },
      });
    });
  };

  deleteItem = currentClient => {
    const { dispatch } = this.props;

    dispatch({
      type: 'services/submit',
      payload: {
        type: 'removeClient',
        currentClient },
    });
  };

  render() {
    const {
      services: { list },
      loading,
    } = this.props;
    const {
      form: { getFieldDecorator },
    } = this.props;
    const { visible, done, current = {} } = this.state;

    const editAndDelete = (key, currentItem) => {
      if (key === 'edit') this.showEditModal(currentItem);
      else if (key === 'delete') {
        Modal.confirm({
          title: 'Delete Service',
          content: 'Are you sure？',
          okText: 'Sure',
          cancelText: 'Not Yet',
          onOk: () => this.deleteItem(currentItem),
        });
      }
    };

    const modalFooter = done
      ? { footer: null, onCancel: this.handleDone }
      : { okText: 'Ok', onOk: this.handleSubmit, onCancel: this.handleCancel };

    const extraContent = (
      <div className={styles.extraContent}>
        <Search className={styles.extraContentSearch} placeholder="Search" onSearch={() => ({})} />
      </div>
    );

    const paginationProps = {
      showSizeChanger: true,
      showQuickJumper: true,
      pageSize: 10,
      total: 10,
    };


    const ListUsers = ({ data: { roles } }) => (
      <div className={styles.listContent}>
        <div className={styles.listContentItem}>
          <span style={{ float: 'right', margin: '0px 8px 0px 0px' }}><b>Users On Services</b></span>
          <br />
          <a>12 Users</a>
        </div>
      </div>
    );

    const ListContent = ({ data: { roles } }) => (
      <div className={styles.listContent}>
        <div className={styles.listContentItem}>
          <span style={{ float: 'right', margin: '0px 8px 0px 0px' }}><b>All Roles On Services</b></span>
          <br />
          {roles.map(role => (
            <Tag key={role}>{role}</Tag>
          ))}
        </div>
      </div>
    );

    const EditActionBtn = props => (
      <a onClick={e => {
        e.preventDefault();
        editAndDelete("edit", props.current)}
      }
      >
        Edit
      </a>
    );

    const DeleteActionBtn = props => (
      <a onClick={e => {
        e.preventDefault();
        editAndDelete("delete", props)}
      }
      >
        Delete
      </a>
    );

    const getModalContent = () => {
      if (done) {
        return (
          <Result
            type="success"
            title="Service Was Created Successfully"
            description="Service Was Updated Successfully. Please Recheck..."
            actions={
              <Button type="primary" onClick={this.handleDone}>
                OK
              </Button>
            }
            className={styles.formResult}
          />
        );
      }

      const roles = [];
      if(current.role != null) {
        current.roles.map(role => {
          return roles.push(<SelectOption key={role}>{role}</SelectOption>);
        });
      }

      return (
        <Form onSubmit={this.handleSubmit}>

          <FormItem label="Service Name" {...this.formLayout}>
            {getFieldDecorator('title', {
              rules: [{ required: true, message: 'This field is required' }],
              initialValue: current.title,
            })(<Input placeholder="Service Name" />)}
          </FormItem>
          <FormItem label="Service URL" {...this.formLayout}>
            {getFieldDecorator('href', {
              rules: [{ required: true, message: 'This field is required' }],
              initialValue: current.href,
            })(<Input placeholder="Service URL" />)}
          </FormItem>
          <FormItem label="Service Path" {...this.formLayout}>
            {getFieldDecorator('path', {
              rules: [{ required: true, message: 'This field is required' }],
              initialValue: current.path,
            })(<Input placeholder="Service Path" />)}
          </FormItem>
          <FormItem label="Role" {...this.formLayout}>
            {getFieldDecorator('roles', {
              rules: [{ required: true, message: 'This field is required' }],
              initialValue: current.roles,
            })(
              <Select mode="tags" placeholder="Please select user">
                {roles}
              </Select>
            )}
          </FormItem>
          <FormItem label="Protected?" {...this.formLayout}>
            {getFieldDecorator('oidc', {
              initialValue: current.oidc ? current.oidc : false,
              valuePropName: 'checked',
            })(
                <Switch checkedChildren={<Icon type="check"/>}
                        unCheckedChildren={<Icon type="close"/>}
                />
            )}
          </FormItem>
          <FormItem {...this.formLayout} label="Description">
            {getFieldDecorator('subDescription', {
              rules: [{ message: 'Description Minlength Is 5', min: 5 }],
              initialValue: current.subDescription,
            })(<TextArea rows={4} placeholder="Description..." />)}
          </FormItem>
        </Form>
      );
    };
    return (
      <PageHeaderWrapper>
        <div className={styles.standardList}>

          <Card
            className={styles.listCard}
            bordered={false}
            title="All Admin Services"
            style={{ marginTop: 24 }}
            bodyStyle={{ padding: '0 32px 40px 32px' }}
            extra={extraContent}
          >
            <Button
              type="dashed"
              style={{ width: '100%', marginBottom: 8 }}
              icon="plus"
              onClick={this.showModal}
              ref={component => {
                /* eslint-disable */
                this.addBtn = findDOMNode(component);
                /* eslint-enable */
              }}
            >
              Add Service
            </Button>
            <List
              size="large"
              rowKey="id"
              loading={loading}
              pagination={paginationProps}
              dataSource={list}
              renderItem={item => (
                <List.Item
                  actions={[
                    <EditActionBtn current={item} />,
                    <DeleteActionBtn current={item} />,
                  ]}
                >
                  <List.Item.Meta
                    avatar={<Avatar src={item.avatar} shape="square" size="large" />}
                    title={<a href={item.href}>{item.title}</a>}
                    description={item.subDescription}
                  />
                  <ListContent data={item} />
                  <ListUsers data={item} />
                </List.Item>
              )}
            />
          </Card>
        </div>
        <Modal
          title={done ? null : `Service Detail`}
          className={styles.standardListForm}
          width={640}
          bodyStyle={done ? { padding: '72px 0' } : { padding: '28px 0 0' }}
          destroyOnClose
          visible={visible}
          {...modalFooter}
        >
          {getModalContent()}
        </Modal>
      </PageHeaderWrapper>
    );
  }
}

export default ListServices;
