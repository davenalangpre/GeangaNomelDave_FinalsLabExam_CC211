/**
 * OptiVue Eyewear - Frontend Application Logic
 * Features:
 * 1. Sample Eyewear Product Catalog & Dynamic Rendering (Home & Catalog)
 * 2. Multi-Criteria Frame Filtering, Search, Sorting & Spec Modal (Catalog)
 * 3. Persistent Shopping Cart with localStorage & Quantity Controls (All Pages)
 * 4. Validated Optical Prescription & Order Checkout Form (Checkout)
 */

'use strict';

/* ==========================================================================
   1. Sample Eyewear Product Dataset
   ========================================================================== */
const EYEWEAR_PRODUCTS = [
  {
    id: 'ov-101',
    name: 'Aether Titanium Round',
    category: 'prescription',
    categoryLabel: 'Prescription',
    price: 129.00,
    featured: true,
    material: 'Japanese Beta-Titanium',
    shape: 'Round P3',
    dimensions: '49-20-145 mm',
    image: 'https://images.unsplash.com/photo-1574258495973-f010dfbb5371?auto=format&fit=crop&w=700&q=80',
    description: 'Featherweight hypoallergenic titanium frame engineered with adjustable silicone nose pads for balanced all-day wear.'
  },
  {
    id: 'ov-102',
    name: 'Solstice Polarized Aviator',
    category: 'sunglasses',
    categoryLabel: 'Sunglasses',
    price: 145.00,
    featured: true,
    material: 'Brushed Stainless Steel',
    shape: 'Classic Aviator',
    dimensions: '55-16-140 mm',
    image: 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=700&q=80',
    description: 'Double-bridge aviator fitted with CR-39 polarized lenses that eliminate harsh road and water glare.'
  },
  {
    id: 'ov-103',
    name: 'Koda Blue-Shield Square',
    category: 'blue-light',
    categoryLabel: 'Blue-Light',
    price: 89.00,
    featured: true,
    material: 'Hand-Cured Cellulose Acetate',
    shape: 'Architectural Square',
    dimensions: '52-18-145 mm',
    image: 'https://images.unsplash.com/photo-1591076482161-42ce6da69f67?auto=format&fit=crop&w=700&q=80',
    description: 'Filters 45% of high-energy HEV blue light emitted by monitors while preserving true color accuracy.'
  },
  {
    id: 'ov-104',
    name: 'Vesper Tortoise Wayfarer',
    category: 'prescription',
    categoryLabel: 'Prescription',
    price: 115.00,
    featured: true,
    material: 'Italian Mazzucchelli Acetate',
    shape: 'Modified Wayfarer',
    dimensions: '51-19-145 mm',
    image: 'https://images.unsplash.com/photo-1509695507497-903c140c43b0?auto=format&fit=crop&w=700&q=80',
    description: 'Rich Havana tortoiseshell silhouette reinforced with five-barrel stainless steel hinges.'
  },
  {
    id: 'ov-105',
    name: 'Helios Amber Sun Clubmaster',
    category: 'sunglasses',
    categoryLabel: 'Sunglasses',
    price: 138.00,
    featured: false,
    material: 'Acetate & Gold-Tone Alloy',
    shape: 'Browline',
    dimensions: '51-21-145 mm',
    image: 'https://images.unsplash.com/photo-1572635196237-14b3f281503f?auto=format&fit=crop&w=700&q=80',
    description: 'Retro-modern browline sunglasses featuring warm amber gradient UV400 lenses.'
  },
  {
    id: 'ov-106',
    name: 'Lumen Zero-Strain Clear',
    category: 'blue-light',
    categoryLabel: 'Blue-Light',
    price: 79.00,
    featured: false,
    material: 'TR-90 Flexible Polymer',
    shape: 'Keyhole Round',
    dimensions: '48-20-142 mm',
    image: 'https://images.unsplash.com/photo-1577803645773-f96470509666?auto=format&fit=crop&w=700&q=80',
    description: 'Ultra-flexible crystal frame designed for long coding, design, and study sessions.'
  },
  {
    id: 'ov-107',
    name: 'Meridian Minimalist Wire',
    category: 'prescription',
    categoryLabel: 'Prescription',
    price: 134.00,
    featured: false,
    material: 'Matte Gunmetal Titanium',
    shape: 'Geometric Hex',
    dimensions: '50-19-145 mm',
    image: 'https://images.unsplash.com/photo-1508296695146-257a814070b4?auto=format&fit=crop&w=700&q=80',
    description: 'Subtle hexagonal rim profile offering clean geometric lines without added weight.'
  },
  {
    id: 'ov-108',
    name: 'Calypso Oversized Shield',
    category: 'sunglasses',
    categoryLabel: 'Sunglasses',
    price: 152.00,
    featured: false,
    material: 'Bio-Based Acetate',
    shape: 'Soft Cat-Eye',
    dimensions: '54-17-140 mm',
    image: 'https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=700&q=80',
    description: 'Wide-coverage sculpted sun frame with anti-reflective inner lens coating.'
  }
];

const CART_STORAGE_KEY = 'optivue_cart_v1';

/* ==========================================================================
   2. Shopping Cart State & localStorage Helpers
   ========================================================================== */

/**
 * Retrieve cart array safely from localStorage.
 * @returns {Array<{id: string, quantity: number}>}
 */
function getCart() {
  try {
    const raw = localStorage.getItem(CART_STORAGE_KEY);
    const parsed = raw ? JSON.parse(raw) : [];
    return Array.isArray(parsed) ? parsed : [];
  } catch (error) {
    console.error('Error reading cart from localStorage:', error);
    return [];
  }
}

/**
 * Persist cart array to localStorage and refresh navbar badge.
 * @param {Array<{id: string, quantity: number}>} cart
 */
function saveCart(cart) {
  try {
    localStorage.setItem(CART_STORAGE_KEY, JSON.stringify(cart));
  } catch (error) {
    console.error('Error saving cart to localStorage:', error);
  }
  updateCartBadge();
}

/**
 * Update the global cart item counter badge in the header.
 */
function updateCartBadge() {
  const badge = document.getElementById('cartBadge');
  if (!badge) return;

  const totalItems = getCart().reduce((sum, item) => sum + item.quantity, 0);
  badge.innerHTML = `${totalItems}<span class="visually-hidden"> items in cart</span>`;
}

/**
 * Add a product by ID to the cart and show user feedback toast.
 * @param {string} productId
 */
function addToCart(productId) {
  const product = EYEWEAR_PRODUCTS.find((item) => item.id === productId);
  if (!product) return;

  const cart = getCart();
  const existingItem = cart.find((item) => item.id === productId);

  if (existingItem) {
    existingItem.quantity += 1;
  } else {
    cart.push({ id: productId, quantity: 1 });
  }

  saveCart(cart);
  showToast(`Added "${product.name}" to your cart.`);
}

/**
 * Update quantity or remove item from cart.
 * @param {string} productId
 * @param {number} delta (+1 or -1)
 */
function updateItemQuantity(productId, delta) {
  let cart = getCart();
  const itemIndex = cart.findIndex((item) => item.id === productId);
  if (itemIndex === -1) return;

  cart[itemIndex].quantity += delta;
  if (cart[itemIndex].quantity <= 0) {
    cart.splice(itemIndex, 1);
  }

  saveCart(cart);
  renderCheckoutCart();
}

/**
 * Remove a specific item completely from cart.
 * @param {string} productId
 */
function removeCartItem(productId) {
  const updatedCart = getCart().filter((item) => item.id !== productId);
  saveCart(updatedCart);
  renderCheckoutCart();
}

/**
 * Display Bootstrap toast message when user adds an item.
 * @param {string} message
 */
function showToast(message) {
  const toastEl = document.getElementById('cartToast');
  const toastMsgEl = document.getElementById('toastMessage');
  if (!toastEl || !toastMsgEl || typeof bootstrap === 'undefined') return;

  toastMsgEl.textContent = message;
  const toastInstance = bootstrap.Toast.getOrCreateInstance(toastEl, { delay: 2500 });
  toastInstance.show();
}

/* ==========================================================================
   3. Card Rendering (Shared between Home & Catalog)
   ========================================================================== */

/**
 * Create HTML markup for a single product card.
 * @param {Object} product
 * @param {boolean} showDetailsButton
 * @returns {string}
 */
function createProductCardHTML(product, showDetailsButton = false) {
  return `
    <div class="col-sm-6 col-lg-3">
      <article class="card product-card h-100">
        <div class="product-img-wrapper">
          <img src="${product.image}" alt="${product.name} - ${product.shape}" loading="lazy" />
          <span class="badge bg-navy position-absolute top-0 start-0 m-3">${product.categoryLabel}</span>
        </div>
        <div class="card-body d-flex flex-column p-3 p-md-4">
          <div class="d-flex justify-content-between align-items-start gap-2 mb-1">
            <h3 class="h6 fw-bold text-navy mb-0">${product.name}</h3>
            <span class="fw-bold text-teal">$${product.price.toFixed(2)}</span>
          </div>
          <p class="text-secondary extra-small mb-2">${product.material}</p>
          <p class="text-secondary small mb-3 flex-grow-1">${product.description}</p>
          <div class="d-grid gap-2 mt-auto">
            <button type="button" class="btn btn-primary-custom btn-sm py-2 fw-semibold add-to-cart-btn" data-id="${product.id}">
              <i class="bi bi-bag-plus me-1" aria-hidden="true"></i> Add to Cart
            </button>
            ${
              showDetailsButton
                ? `<button type="button" class="btn btn-outline-secondary btn-sm view-specs-btn" data-id="${product.id}">
                    Frame Specs
                  </button>`
                : ''
            }
          </div>
        </div>
      </article>
    </div>
  `;
}

/**
 * Initialize Home Page Featured Grid.
 */
function initHomePage() {
  const featuredGrid = document.getElementById('featuredGrid');
  if (!featuredGrid) return;

  const featuredProducts = EYEWEAR_PRODUCTS.filter((p) => p.featured);
  featuredGrid.innerHTML = featuredProducts.map((p) => createProductCardHTML(p, false)).join('');
}

/* ==========================================================================
   4. Catalog Page: Category Filtering, Search, Sort & Spec Modal
   ========================================================================== */
function initCatalogPage() {
  const catalogGrid = document.getElementById('catalogGrid');
  if (!catalogGrid) return;

  const filterButtons = document.querySelectorAll('.filter-btn');
  const searchInput = document.getElementById('catalogSearch');
  const sortSelect = document.getElementById('catalogSort');
  const resultsCount = document.getElementById('resultsCount');
  const resetBtn = document.getElementById('resetFiltersBtn');

  // Check URL query parameter (e.g., catalog.html?category=sunglasses)
  const urlParams = new URLSearchParams(window.location.search);
  const initialCategory = urlParams.get('category') || 'all';

  let state = {
    category: ['all', 'prescription', 'sunglasses', 'blue-light'].includes(initialCategory)
      ? initialCategory
      : 'all',
    search: '',
    sort: 'featured'
  };

  // Sync active pill button with initial state
  filterButtons.forEach((btn) => {
    btn.classList.toggle('active', btn.dataset.category === state.category);
  });

  function renderCatalog() {
    let filtered = [...EYEWEAR_PRODUCTS];

    // 1. Category Filter
    if (state.category !== 'all') {
      filtered = filtered.filter((item) => item.category === state.category);
    }

    // 2. Keyword Search Filter
    if (state.search.trim() !== '') {
      const q = state.search.toLowerCase().trim();
      filtered = filtered.filter(
        (item) =>
          item.name.toLowerCase().includes(q) ||
          item.material.toLowerCase().includes(q) ||
          item.shape.toLowerCase().includes(q) ||
          item.description.toLowerCase().includes(q)
      );
    }

    // 3. Price Sorting
    if (state.sort === 'price-asc') {
      filtered.sort((a, b) => a.price - b.price);
    } else if (state.sort === 'price-desc') {
      filtered.sort((a, b) => b.price - a.price);
    }

    // Update UI Counter & Reset Button
    resultsCount.textContent = `Showing ${filtered.length} frame${filtered.length === 1 ? '' : 's'}`;
    const isFiltered = state.category !== 'all' || state.search.trim() !== '' || state.sort !== 'featured';
    resetBtn.classList.toggle('d-none', !isFiltered);

    // Empty State vs Product Cards
    if (filtered.length === 0) {
      catalogGrid.innerHTML = `
        <div class="col-12">
          <div class="text-center bg-white p-5 rounded-4 border">
            <i class="bi bi-eyeglasses fs-1 text-secondary mb-2 d-block" aria-hidden="true"></i>
            <h2 class="h5 fw-bold text-navy">No matching frames found</h2>
            <p class="text-secondary small mb-3">Try adjusting your search keywords or switching frame categories.</p>
            <button type="button" class="btn btn-primary-custom btn-sm px-4" id="emptyResetBtn">Show All Frames</button>
          </div>
        </div>
      `;
      document.getElementById('emptyResetBtn')?.addEventListener('click', resetAllFilters);
      return;
    }

    catalogGrid.innerHTML = filtered.map((p) => createProductCardHTML(p, true)).join('');
  }

  function resetAllFilters() {
    state = { category: 'all', search: '', sort: 'featured' };
    searchInput.value = '';
    sortSelect.value = 'featured';
    filterButtons.forEach((btn) => {
      btn.classList.toggle('active', btn.dataset.category === 'all');
    });
    renderCatalog();
  }

  // Event Listeners
  filterButtons.forEach((btn) => {
    btn.addEventListener('click', () => {
      filterButtons.forEach((b) => b.classList.remove('active'));
      btn.classList.add('active');
      state.category = btn.dataset.category;
      renderCatalog();
    });
  });

  searchInput.addEventListener('input', (e) => {
    state.search = e.target.value;
    renderCatalog();
  });

  sortSelect.addEventListener('change', (e) => {
    state.sort = e.target.value;
    renderCatalog();
  });

  resetBtn.addEventListener('click', resetAllFilters);

  // Frame Specs Modal Handler (Event Delegation)
  catalogGrid.addEventListener('click', (e) => {
    const specsBtn = e.target.closest('.view-specs-btn');
    if (!specsBtn) return;

    const product = EYEWEAR_PRODUCTS.find((p) => p.id === specsBtn.dataset.id);
    if (!product) return;

    document.getElementById('modalFrameName').textContent = product.name;
    document.getElementById('modalFrameImg').src = product.image;
    document.getElementById('modalFrameImg').alt = product.name;
    document.getElementById('modalFrameCategory').textContent = product.categoryLabel;
    document.getElementById('modalFramePrice').textContent = `$${product.price.toFixed(2)}`;
    document.getElementById('modalFrameDesc').textContent = product.description;
    document.getElementById('modalFrameMaterial').textContent = product.material;
    document.getElementById('modalFrameShape').textContent = product.shape;
    document.getElementById('modalFrameDimensions').textContent = product.dimensions;

    const modalAddBtn = document.getElementById('modalAddToCartBtn');
    modalAddBtn.dataset.id = product.id;

    const modalEl = document.getElementById('productDetailModal');
    const modalInstance = bootstrap.Modal.getOrCreateInstance(modalEl);
    modalInstance.show();
  });

  // Modal Add to Cart Button
  document.getElementById('modalAddToCartBtn')?.addEventListener('click', (e) => {
    const id = e.currentTarget.dataset.id;
    if (id) {
      addToCart(id);
      const modalEl = document.getElementById('productDetailModal');
      bootstrap.Modal.getInstance(modalEl)?.hide();
    }
  });

  renderCatalog();
}

/* ==========================================================================
   5. Checkout Page: Cart Rendering & Validated Prescription Form
   ========================================================================== */

/**
 * Render dynamic cart items and calculate order totals on Checkout page.
 */
function renderCheckoutCart() {
  const container = document.getElementById('cartItemsContainer');
  const totalsBox = document.getElementById('cartTotalsBox');
  const clearCartBtn = document.getElementById('clearCartBtn');
  if (!container || !totalsBox) return;

  const cart = getCart();

  if (cart.length === 0) {
    totalsBox.classList.add('d-none');
    clearCartBtn?.classList.add('d-none');
    container.innerHTML = `
      <div class="text-center py-4">
        <i class="bi bi-bag-x fs-1 text-secondary d-block mb-2" aria-hidden="true"></i>
        <p class="fw-semibold text-navy mb-1">Your shopping bag is empty</p>
        <p class="text-secondary small mb-3">Add at least one eyewear frame before checking out.</p>
        <a href="catalog.html" class="btn btn-primary-custom btn-sm px-4">Browse Frames</a>
      </div>
    `;
    return;
  }

  totalsBox.classList.remove('d-none');
  clearCartBtn?.classList.remove('d-none');

  let subtotal = 0;

  container.innerHTML = cart
    .map((cartItem) => {
      const product = EYEWEAR_PRODUCTS.find((p) => p.id === cartItem.id);
      if (!product) return '';

      const lineTotal = product.price * cartItem.quantity;
      subtotal += lineTotal;

      return `
        <div class="d-flex align-items-center gap-3 py-3 border-bottom">
          <img src="${product.image}" alt="${product.name}" class="cart-thumb" />
          <div class="flex-grow-1 min-w-0">
            <h3 class="h6 fw-bold text-navy mb-0 text-truncate">${product.name}</h3>
            <p class="text-secondary extra-small mb-2">$${product.price.toFixed(2)} each</p>
            <div class="d-flex align-items-center gap-2">
              <button type="button" class="btn btn-outline-secondary btn-sm px-2 py-0 qty-btn" data-id="${product.id}" data-delta="-1" aria-label="Decrease quantity of ${product.name}">−</button>
              <span class="small fw-semibold px-1">${cartItem.quantity}</span>
              <button type="button" class="btn btn-outline-secondary btn-sm px-2 py-0 qty-btn" data-id="${product.id}" data-delta="1" aria-label="Increase quantity of ${product.name}">+</button>
              <button type="button" class="btn btn-link btn-sm text-danger text-decoration-none ms-2 p-0 remove-item-btn" data-id="${product.id}">Remove</button>
            </div>
          </div>
          <div class="fw-bold text-navy small">$${lineTotal.toFixed(2)}</div>
        </div>
      `;
    })
    .join('');

  // Calculate Lens Upgrade & Shipping
  const lensSelect = document.getElementById('lensPackage');
  const selectedOption = lensSelect ? lensSelect.options[lensSelect.selectedIndex] : null;
  const lensPricePerUnit = selectedOption ? parseFloat(selectedOption.dataset.price || '0') : 0;
  const totalFramesCount = cart.reduce((sum, item) => sum + item.quantity, 0);
  const lensUpgradeTotal = lensPricePerUnit * totalFramesCount;
  const shippingFee = subtotal >= 150 ? 0 : 12.00;
  const grandTotal = subtotal + lensUpgradeTotal + shippingFee;

  document.getElementById('subtotalAmount').textContent = `$${subtotal.toFixed(2)}`;
  document.getElementById('lensUpgradeAmount').textContent = `$${lensUpgradeTotal.toFixed(2)}`;
  document.getElementById('shippingAmount').textContent = shippingFee === 0 ? 'FREE' : `$${shippingFee.toFixed(2)}`;
  document.getElementById('totalAmount').textContent = `$${grandTotal.toFixed(2)}`;
}

/**
 * Initialize Checkout Page Cart Listeners and Form Validation.
 */
function initCheckoutPage() {
  const checkoutForm = document.getElementById('checkoutForm');
  const cartContainer = document.getElementById('cartItemsContainer');
  if (!checkoutForm || !cartContainer) return;

  renderCheckoutCart();

  // Quantity & Remove Button Delegation
  cartContainer.addEventListener('click', (e) => {
    const qtyBtn = e.target.closest('.qty-btn');
    if (qtyBtn) {
      updateItemQuantity(qtyBtn.dataset.id, parseInt(qtyBtn.dataset.delta, 10));
      return;
    }

    const removeBtn = e.target.closest('.remove-item-btn');
    if (removeBtn) {
      removeCartItem(removeBtn.dataset.id);
    }
  });

  // Clear Entire Cart
  document.getElementById('clearCartBtn')?.addEventListener('click', () => {
    saveCart([]);
    renderCheckoutCart();
  });

  // Recalculate totals when lens package changes
  const lensSelect = document.getElementById('lensPackage');
  lensSelect.addEventListener('change', renderCheckoutCart);

  // Helper to toggle Bootstrap validation classes
  function setFieldValidity(inputEl, isValid) {
    inputEl.classList.toggle('is-valid', isValid);
    inputEl.classList.toggle('is-invalid', !isValid);
    return isValid;
  }

  // Validate diopter input (must be within -12.00 to +12.00 and divisible by 0.25)
  function isValidDiopter(valStr) {
    if (valStr.trim() === '') return false;
    const num = parseFloat(valStr);
    if (Number.isNaN(num) || num < -12 || num > 12) return false;
    return Math.abs((num * 100) % 25) === 0;
  }

  // Validate entire checkout form
  function validateCheckoutForm() {
    let isFormValid = true;

    const fullName = document.getElementById('fullName');
    const email = document.getElementById('emailAddress');
    const phone = document.getElementById('phoneNumber');
    const city = document.getElementById('shippingCity');
    const address = document.getElementById('shippingAddress');

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const phoneDigits = phone.value.replace(/\D/g, '');

    if (!setFieldValidity(fullName, fullName.value.trim().length >= 2)) isFormValid = false;
    if (!setFieldValidity(email, emailRegex.test(email.value.trim()))) isFormValid = false;
    if (!setFieldValidity(phone, phoneDigits.length >= 7 && phoneDigits.length <= 15)) isFormValid = false;
    if (!setFieldValidity(city, city.value.trim().length >= 3)) isFormValid = false;
    if (!setFieldValidity(address, address.value.trim().length >= 5)) isFormValid = false;

    // Validate Prescription Fields if an Rx lens package is selected
    const requiresRx = lensSelect.value !== 'non-rx';
    const odSphere = document.getElementById('odSphere');
    const osSphere = document.getElementById('osSphere');
    const pdValue = document.getElementById('pdValue');

    if (requiresRx) {
      if (!setFieldValidity(odSphere, isValidDiopter(odSphere.value))) isFormValid = false;
      if (!setFieldValidity(osSphere, isValidDiopter(osSphere.value))) isFormValid = false;

      const pdNum = parseFloat(pdValue.value);
      const validPd = !Number.isNaN(pdNum) && pdNum >= 50 && pdNum <= 78;
      if (!setFieldValidity(pdValue, validPd)) isFormValid = false;
    } else {
      [odSphere, osSphere, pdValue].forEach((el) => el.classList.remove('is-valid', 'is-invalid'));
    }

    return isFormValid;
  }

  // Form Submission Handler
  checkoutForm.addEventListener('submit', (e) => {
    e.preventDefault();

    const errorBanner = document.getElementById('formErrorBanner');
    const errorText = document.getElementById('formErrorText');
    const confirmationAlert = document.getElementById('orderConfirmationAlert');

    const cart = getCart();
    if (cart.length === 0) {
      errorText.textContent = 'Your cart is empty. Please add at least one frame from the catalog before submitting.';
      errorBanner.classList.remove('d-none');
      errorBanner.scrollIntoView({ behavior: 'smooth', block: 'center' });
      return;
    }

    const isValid = validateCheckoutForm();
    if (!isValid) {
      errorText.textContent = 'Please correct the highlighted fields below before completing your order.';
      errorBanner.classList.remove('d-none');
      checkoutForm.querySelector('.is-invalid')?.focus();
      return;
    }

    // Valid Order Execution
    errorBanner.classList.add('d-none');
    const customerName = document.getElementById('fullName').value.trim();
    const customerEmail = document.getElementById('emailAddress').value.trim();
    const totalPaid = document.getElementById('totalAmount').textContent;
    const orderRef = `OV-${Math.floor(100000 + Math.random() * 900000)}`;

    document.getElementById('orderRefNumber').textContent = orderRef;
    document.getElementById('orderConfirmationDetails').textContent =
      `Thank you, ${customerName}! Your optical order (${totalPaid}) has been queued for lab verification. A digital receipt and prescription summary were sent to ${customerEmail}.`;

    confirmationAlert.classList.remove('d-none');
    confirmationAlert.scrollIntoView({ behavior: 'smooth', block: 'center' });

    // Clear cart & reset form states
    saveCart([]);
    renderCheckoutCart();
    checkoutForm.reset();
    checkoutForm.querySelectorAll('.is-valid, .is-invalid').forEach((el) => {
      el.classList.remove('is-valid', 'is-invalid');
    });
  });
}

/* ==========================================================================
   6. Global Initialization on DOMContentLoaded
   ========================================================================== */
document.addEventListener('DOMContentLoaded', () => {
  updateCartBadge();
  initHomePage();
  initCatalogPage();
  initCheckoutPage();

  // Global Event Delegation for "Add to Cart" buttons on Home & Catalog
  document.body.addEventListener('click', (e) => {
    const addBtn = e.target.closest('.add-to-cart-btn');
    if (addBtn) {
      addToCart(addBtn.dataset.id);
    }
  });
});