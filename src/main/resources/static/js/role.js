const addRoleButton = document.querySelector('.fudong');
const modals = document.querySelector('.modals');
const modalsTitle = document.querySelector('.modals-title');

addRoleButton.addEventListener('click', function() {
  modalsTitle.innerHTML = '添加角色';
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
  modals.style.display = "none";
});

const editRole = document.querySelector('.edit-role');
editRole.addEventListener('click', function() {
  modalsTitle.innerHTML = '编辑角色';
  if (modals.style.display === 'none') {
    modals.style.display = 'block';
  } else {
    modals.style.display = 'none';
  }
});

const allMember = document.querySelector('.all-member');
const member = document.querySelectorAll('.member');
allMember.addEventListener('click', function() {
  const allChecked = this.checked;
  member.forEach(ele => {
    ele.checked = allChecked;
  });
});
