const addPerButton = document.querySelector('.fudong');
const modals = document.querySelector('.modals');
const modalsTitle = document.querySelector('.modals-title');

addPerButton.addEventListener('click', function() {
  modalsTitle.innerHTML = '添加权限';
  if (modals.style.display === 'none') {
    modals.style.display = 'block';
  } else {
    modals.style.display = 'none';
  }
});

const closeButton = document.querySelector('.black');
closeButton.addEventListener('click', function() {
  modals.style.display = 'none';
});

const closeButton2 = document.querySelector('.qx');
closeButton2.addEventListener('click', function() {
  modals.style.display = 'none';
});

const editPerButton = document.querySelector('.edit-per');
editPerButton.addEventListener('click', function() {
  modalsTitle.innerHTML = '编辑权限';
  modals.style.display = 'block';
});

const allMember = document.querySelector('.all-member');
const member = document.querySelectorAll('.member');
allMember.addEventListener('click', function() {
  const allChecked = this.checked
  member.forEach(ele => {
    ele.checked = allChecked;
  });
});

const allrole = document.querySelector('.all-role');
const role = document.querySelectorAll('.role');
allrole.addEventListener('click', function() {
  const allChecked = this.checked
  role.forEach(ele => {
    ele.checked = allChecked;
  });
});
