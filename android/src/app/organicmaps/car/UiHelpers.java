package app.organicmaps.car;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.car.app.CarContext;
import androidx.car.app.ScreenManager;
import androidx.car.app.model.Action;
import androidx.car.app.model.ActionStrip;
import androidx.car.app.model.CarColor;
import androidx.car.app.model.CarIcon;
import androidx.car.app.navigation.model.MapController;
import androidx.core.graphics.drawable.IconCompat;

import app.organicmaps.R;
import app.organicmaps.car.screens.settings.SettingsScreen;
import app.organicmaps.location.LocationHelper;
import app.organicmaps.location.LocationState;

public final class UiHelpers
{
  public static ActionStrip createSettingsActionStrip(@NonNull CarContext context, @NonNull SurfaceRenderer surfaceRenderer)
  {
    final CarIcon iconSettings = new CarIcon.Builder(IconCompat.createWithResource(context, R.drawable.ic_settings)).build();
    final Action settings = new Action.Builder().setIcon(iconSettings).setOnClickListener(
        () -> context.getCarService(ScreenManager.class).push(new SettingsScreen(context, surfaceRenderer))
    ).build();
    return new ActionStrip.Builder().addAction(settings).build();
  }

  public static MapController createMapController(@NonNull CarContext context, @NonNull SurfaceRenderer surfaceRenderer)
  {
    final CarIcon iconPlus = new CarIcon.Builder(IconCompat.createWithResource(context, R.drawable.ic_plus)).build();
    final CarIcon iconMinus = new CarIcon.Builder(IconCompat.createWithResource(context, R.drawable.ic_minus)).build();

    final Action panAction = new Action.Builder(Action.PAN).build();
    final Action location = createLocationButton(context);
    final Action zoomIn = new Action.Builder().setIcon(iconPlus).setOnClickListener(surfaceRenderer::onZoomIn).build();
    final Action zoomOut = new Action.Builder().setIcon(iconMinus).setOnClickListener(surfaceRenderer::onZoomOut).build();
    final ActionStrip mapActionStrip = new ActionStrip.Builder()
        .addAction(panAction)
        .addAction(zoomIn)
        .addAction(zoomOut)
        .addAction(location)
        .build();
    return new MapController.Builder().setMapActionStrip(mapActionStrip).build();
  }

  private static Action createLocationButton(@NonNull CarContext context)
  {
    final Action.Builder builder = new Action.Builder();
    final int locationMode = LocationHelper.INSTANCE.getMyPositionMode();
    CarColor tintColor = CarColor.DEFAULT;

    @DrawableRes int drawableRes;
    switch (locationMode)
    {
    case LocationState.PENDING_POSITION:
    case LocationState.NOT_FOLLOW_NO_POSITION:
      drawableRes = R.drawable.ic_location_off;
      break;
    case LocationState.NOT_FOLLOW:
      drawableRes = R.drawable.ic_not_follow;
      break;
    case LocationState.FOLLOW:
      drawableRes = R.drawable.ic_follow;
      tintColor = CarColor.BLUE;
      break;
    case LocationState.FOLLOW_AND_ROTATE:
      drawableRes = R.drawable.ic_follow_and_rotate;
      tintColor = CarColor.BLUE;
      break;
    default:
      throw new IllegalArgumentException("Invalid button mode: " + locationMode);
    }

    final CarIcon icon = new CarIcon.Builder(IconCompat.createWithResource(context, drawableRes)).setTint(tintColor).build();
    builder.setIcon(icon);
    builder.setOnClickListener(() -> {
      LocationState.nativeSwitchToNextMode();
      if (!LocationHelper.INSTANCE.isActive())
        LocationHelper.INSTANCE.start();
    });
    return builder.build();
  }
}
