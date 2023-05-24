const editDpButton = document.querySelector('.edit-dp');
const modals = document.querySelector('.modals');
const mask = document.querySelector('.mask');
const addDpmodal = document.querySelector('.add-dp-modal');

editDpButton.addEventListener('click', function() {
  if (modals.style.display === 'none') {
    modals.style.display = "block";
  }
});

const closeButton = document.querySelector('.black');
closeButton.addEventListener('click', function() {
  modals.style.display = "none";
});

const closeButton2 = document.querySelector('.close-modals');
closeButton2.addEventListener('click', function() {
  modals.style.display = "none";
});

const addDpButtons = document.querySelectorAll('.add');
addDpButtons.forEach(ele => {
  ele.addEventListener('click', function() {
    if (addDpmodal.style.display === 'none') {
      addDpmodal.style.display = "block";
    }
  });
})

const addDpCancel = document.querySelectorAll('.add-dp-cancel');
addDpCancel.forEach(ele => {
  ele.addEventListener('click', function() {
    addDpmodal.style.display = "none";
  });
})

const allMember = document.querySelector('.all-member');
const member = document.querySelectorAll('.member');
allMember.addEventListener('click', function() {
  const allChecked = this.checked;
  member.forEach(ele => {
    ele.checked = allChecked;
  });
});
