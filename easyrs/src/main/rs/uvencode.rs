#pragma version(1)
#pragma rs java_package_name(io.github.silvaren.easyrs.scripts)

rs_allocation gOut;
int width;
int height;

void root(const uchar4 *v_in, const void *usrData, uint32_t x, uint32_t y) {
    uchar v = (*v_in).g;
    uchar u = (*v_in).b;
    rsSetElementAt_uchar(gOut, u, (y * width) + x * 2);
    rsSetElementAt_uchar(gOut, v, (y * width) + x * 2 + 1);

}