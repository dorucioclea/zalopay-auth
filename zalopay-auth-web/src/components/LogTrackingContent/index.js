import React from 'react';
import moment from 'moment';
import { Avatar } from 'antd';
import styles from './index.less';

const LogTrackingContent = ({ data: { queryString, updatedAt, avatar, userLoggedRequest, href } }) => (
  <div className={styles.listContent}>
    <div className={styles.description}>Query Params: {queryString}</div>
    <div className={styles.extra}>
      <Avatar src={avatar} size="small" />
      <a href={href}>{userLoggedRequest}</a> called at&nbsp;
      {moment(updatedAt).format('YYYY-MM-DD HH:mm')}
    </div>
  </div>
);

export default LogTrackingContent;
