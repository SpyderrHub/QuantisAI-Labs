const { google } = require('googleapis');
const fs = require('fs');
const path = require('path');

const key = {
  "type": "service_account",
  "project_id": "studio-7977682669-c03b0",
  "private_key_id": "78081c03762b476a5428756f4c4a7addf93f8a5b",
  "private_key": "-----BEGIN PRIVATE KEY-----\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCrQ8A12RsYjAdT\n2qFWj1mH7jfFJdIDutJdsh07MRP2mzIVrX3G2fqOBxwBK1Du/iuhJfLiBmp+VckM\nZawPoyfysRlCFwp3RURvRNtWEq7Ms47Q8KgMReFVgbaV3Hwo91yywALfoBoFldoh\n8JdeewohNaRPwj8KejHgffHFf4fs6zAwlC57YQOeWajKHv4DXixz7JShQGHTxZGG\nk1syxUmua8oluaFCtzi9u3AraZ8Wdp1DQbACtV24KV2NcjqdW73FdHgGBSAOlfcH\neave8d2Z0WDoQE5nSyLVrUcYfzX716hwSsjVM/tZGO0RjUd287BUottfgkCHfSPu\nhb2ns2h3AgMBAAECggEAK0DToOXhbXwj954ORCW1B1r288/+79k1ettafZICCDSe\n3BGyY8+2cztBa9wsNwezLiI+6q2TJK/2tC5TW6mpk6X9io72IjQ4ud9Kg7z5L5Kj\nnYpB7sORSr6VRdjm9JOwciaDM0YkfEehzRONrxtk7gk1o70bncJJqXg1YNs6EW/U\niEEBhvDMbXZXrZHizt9uFOsOkNRchq3lF95T2ABSqiaBnJG/8uz2TqeiCys+gfq+\n0PD+fqYG2TYz6BvUJxaI1Ye/44Dd3U3HbpqH3zPvU4FInWiNHx0vacl/THbYm/E7\nRmEtXnpNDPCy29w2Uwc5QvKwScSbC/lXgiLBSuwwCQKBgQDei+UgxL1tgt5t0YCD\ntyFc+zg9d9UmuBQquFcXzpS+1zAzWz3KK46XZknxN+EJbR5Ig7xMK8AdqlQNTqY8\nO8cMKvhzDPtzIaMdb375fSHCbbYsNAnjtEf0UXMpowo0IjTgJ5uu3qMzHdwU8DPe\ntegLMJ6vlBpERTTehT8M/Ob+iQKBgQDFAmmYX/MqXG4qmdBYRj+IhvxonDUZW9vI\nyru+5aMWZBwz4wglznFMOb15TxfOHrA1kl5SWCz6N6enRcjDyUSTmMIznfMMq68W\nAFmv33ITJ1VCSeQEPIQVDPyMP6c+FNh08F5uGb4JrP4hEja8wjpbuXk2h3wsKpS/\nVEiUAxVu/wKBgHm/VKk5mFvpxV+UxlbIf8MLWHggL4bQG5BHarYGeM9yj9b4qnQI\noihneDozlLQAcxm+n+uX37Ea9oKVLVI0ba9VbrgaORGCLGc1EOTSNP7V4FyMV4Cv\nptGdIYB8xl9A+2ZJpNzkxal4Q2ddBTNolrpQbe+l+TyyE7tMb/LeBEzpAoGAWLNM\nXcmdjw4Mn+ue7Y+7XToBfQwPsDo2i4IT49A6jINIW2g1q+f2BO7eSzb8LdaNWaFs\nDEEdBw910Y9O5C/DS3z0uw67dpH6A7rmppPBJbSI446nNe0VPvEY6ABkyUPTbVsn\n+QRoFq0nwjYUJQqCAfhRBeccVjpZvIczIkj9JnsCgYEAzFR4nCGAyLc6g5FjsAdo\neqEG4ZVBOR3mCzgp2GbW0YZiWf2K6r29COLd0kEe9EB79Ynxjht0WnndsQMzZo6A\npFXyRAg2bqx+iBtnFUlAFihOj0SpZdGnxIlCJEHEsLn4ILCcB6O9Y7HR/hNd7j39\nBa6FX1h/vsEqxURgKbgvvrY=\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk-fbsvc@studio-7977682669-c03b0.iam.gserviceaccount.com",
  "client_id": "114055446540922442566",
  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
  "token_uri": "https://oauth2.googleapis.com/token",
  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40studio-7977682669-c03b0.iam.gserviceaccount.com",
  "universe_domain": "googleapis.com"
};

async function run() {
  const auth = new google.auth.GoogleAuth({
    credentials: key,
    scopes: ['https://www.googleapis.com/auth/cloud-platform'],
  });

  const firebase = google.firebase({ version: 'v1beta1', auth });
  const projectId = key.project_id;
  const packageName = 'com.aistudio.voiceai.xyzabc';

  try {
    // Check if app already exists
    const apps = await firebase.projects.androidApps.list({ parent: `projects/${projectId}` });
    let app = apps.data.apps?.find(a => a.packageName === packageName);

    if (!app) {
      console.log('Creating Android app...');
      const res = await firebase.projects.androidApps.create({
        parent: `projects/${projectId}`,
        requestBody: {
          packageName: packageName,
          displayName: 'VoiceAI'
        }
      });
      // Wait for operation to complete
      let op = res.data;
      while (!op.done) {
        await new Promise(r => setTimeout(r, 1000));
        const opRes = await auth.request({ url: `https://firebase.googleapis.com/v1beta1/${op.name}` });
        op = opRes.data;
      }
      app = op.response;
    }

    console.log('Getting config...');
    const config = await firebase.projects.androidApps.getConfig({
      name: `${app.name}/config`
    });

    const configData = Buffer.from(config.data.configFilename ? config.data.configFileContents : config.data.configFileContents, 'base64').toString('utf8');
    fs.writeFileSync('/app/applet/app/google-services.json', configData);
    console.log('Saved google-services.json');
    
  } catch (err) {
    console.error(err);
  }
}

run();
